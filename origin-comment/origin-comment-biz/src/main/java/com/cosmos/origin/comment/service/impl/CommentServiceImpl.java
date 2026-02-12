package com.cosmos.origin.comment.service.impl;

import com.cosmos.origin.comment.domain.dos.CommentDO;
import com.cosmos.origin.comment.domain.mapper.CommentMapper;
import com.cosmos.origin.comment.enums.CommentStatusEnum;
import com.cosmos.origin.comment.model.vo.*;
import com.cosmos.origin.comment.service.CommentService;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import toolgood.words.WordsSearch;
import toolgood.words.WordsSearchResult;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final WordsSearch wordsSearch;

    @Value("${comment.examine.open}")
    private Boolean isCommentExamineOpen; // 是否开启评论审核
    @Value("${comment.sensi.word.open}")
    private Boolean isCommentSensiWordOpen; // 是否开启敏感词过滤

    /**
     * 发布评论
     *
     * @param publishCommentReqVO 发布评论请求参数
     * @return 发布评论响应结果
     */
    @Override
    public Response<?> publishComment(PublishCommentReqVO publishCommentReqVO) {
        // 获取存储在 ThreadLocal 中的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 拿到用户名
        String username = authentication.getName();

        // 回复的评论 ID
        Long replyCommentId = publishCommentReqVO.getReplyCommentId();
        // 评论内容
        String content = publishCommentReqVO.getContent();
        // 昵称
        String nickname = publishCommentReqVO.getNickname();

        // 设置默认状态（正常）
        Integer status = CommentStatusEnum.NORMAL.getCode();
        // 审核不通过原因
        String reason = "";

        // 如果开启了审核, 设置状态为待审核，等待博主后台审核通过
        if (isCommentExamineOpen) {
            status = CommentStatusEnum.WAIT_EXAMINE.getCode();
        }

        // 评论内容是否包含敏感词
        boolean isContainSensitiveWord;
        // 是否开启了敏感词过滤
        if (isCommentSensiWordOpen) {
            // 校验评论中是否包含敏感词
            isContainSensitiveWord = wordsSearch.ContainsAny(content);

            if (isContainSensitiveWord) {
                // 若包含敏感词，设置状态为审核不通过
                status = CommentStatusEnum.EXAMINE_FAILED.getCode();
                // 匹配到的所有敏感词组
                List<WordsSearchResult> results = wordsSearch.FindAll(content);
                List<String> keywords = results.stream().map(result -> result.Keyword).collect(Collectors.toList());
                // 不通过的原因
                reason = String.format("系统自动拦截，包含敏感词：%s", keywords);
                log.warn("此评论内容中包含敏感词: {}, content: {}", keywords, content);
            }
        }

        // 构建 DO 对象
        CommentDO commentDO = CommentDO.builder()
                .avatar(publishCommentReqVO.getAvatar())
                .content(content)
                .nickname(nickname)
                .username(username)
                .routerUrl(publishCommentReqVO.getRouterUrl())
                .replyCommentId(replyCommentId)
                .parentCommentId(publishCommentReqVO.getParentCommentId())
                .status(status)
                .reason(reason)
                .build();

        // 新增评论
        commentMapper.insert(commentDO);

        return Response.success();
    }

    /**
     * 查询页面所有评论
     *
     * @param findCommentListReqVO 查询评论列表请求参数
     * @return 查询评论列表响应结果
     */
    @Override
    public Response<?> findCommentList(FindCommentListReqVO findCommentListReqVO) {
        // 路由地址
        String routerUrl = findCommentListReqVO.getRouterUrl();

        // 查询该路由地址下所有评论（仅查询状态正常的）
        List<CommentDO> commentDOS = commentMapper.selectByRouterUrlAndStatus(routerUrl, CommentStatusEnum.NORMAL.getCode());
        // 总评论数
        Integer total = commentDOS.size();

        List<FindCommentItemRspVO> vos = null;
        // DO 转 VO
        if (!CollectionUtils.isEmpty(commentDOS)) {
            // 一级评论
            vos = commentDOS.stream()
                    .filter(commentDO -> Objects.isNull(commentDO.getParentCommentId())) // parentCommentId 父级 ID 为空，则表示为一级评论
                    .map(commentDO -> FindCommentItemRspVO
                            .builder()
                            .id(commentDO.getId())
                            .avatar(commentDO.getAvatar())
                            .nickname(commentDO.getNickname())
                            .content(commentDO.getContent())
                            .createTime(commentDO.getCreateTime())
                            .childComments(null)
                            .isShowReplyForm(false)
                            .build())
                    .collect(Collectors.toList());

            // 循环设置评论回复数据
            vos.forEach(vo -> {
                Long commentId = vo.getId();
                List<FindCommentItemRspVO> childComments = commentDOS.stream()
                        .filter(commentDO -> Objects.equals(commentDO.getParentCommentId(), commentId)) // 过滤出一级评论下所有子评论
                        .sorted(Comparator.comparing(CommentDO::getCreateTime)) // 按发布时间升序排列
                        .map(commentDO -> {
                            FindCommentItemRspVO findPageCommentRspVO = FindCommentItemRspVO.builder()
                                    .id(commentDO.getId())
                                    .avatar(commentDO.getAvatar())
                                    .nickname(commentDO.getNickname())
                                    .content(commentDO.getContent())
                                    .createTime(commentDO.getCreateTime())
                                    .childComments(null)
                                    .isShowReplyForm(false)
                                    .build();
                            Long replyCommentId = commentDO.getReplyCommentId();
                            // 若二级评论的 replayCommentId 不等于一级评论 ID, 前端则需要展示【回复 @ xxx】，需要设置回复昵称
                            if (!Objects.equals(replyCommentId, commentId)) {
                                // 设置回复用户的昵称
                                Optional<CommentDO> optionalCommentDO = commentDOS.stream()
                                        .filter(commentDO1 -> Objects.equals(commentDO1.getId(), replyCommentId)).findFirst();
                                optionalCommentDO.ifPresent(aDo -> findPageCommentRspVO.setReplyNickname(aDo.getNickname()));
                            }
                            return findPageCommentRspVO;
                        }).collect(Collectors.toList());

                vo.setChildComments(childComments);
            });
        }

        return Response.success(FindCommentListRspVO.builder()
                .total(total)
                .comments(vos)
                .build());
    }

    /**
     * 查询评论分页数据
     *
     * @param findCommentPageListReqVO 查询评论分页数据请求参数
     * @return 查询评论分页数据响应结果
     */
    @Override
    public Response<?> findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findCommentPageListReqVO.getCurrent();
        Long size = findCommentPageListReqVO.getSize();
        LocalDate startDate = findCommentPageListReqVO.getStartDate();
        LocalDate endDate = findCommentPageListReqVO.getEndDate();
        String routerUrl = findCommentPageListReqVO.getRouterUrl();
        Integer status = findCommentPageListReqVO.getStatus();

        // 执行分页查询
        Page<CommentDO> commentDOPage = commentMapper.selectPageList(current, size, routerUrl, startDate, endDate, status);

        List<CommentDO> commentDOS = commentDOPage.getRecords();

        // DO 转 VO
        List<FindCommentPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(commentDOS)) {
            vos = commentDOS.stream()
                    .map(commentDO -> FindCommentPageListRspVO.builder()
                            .id(commentDO.getId())
                            .routerUrl(commentDO.getRouterUrl())
                            .avatar(commentDO.getAvatar())
                            .nickname(commentDO.getNickname())
                            .username(commentDO.getUsername())
                            .createTime(commentDO.getCreateTime())
                            .content(commentDO.getContent())
                            .status(commentDO.getStatus())
                            .reason(commentDO.getReason())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(commentDOPage, vos);
    }

    /**
     * 删除评论
     *
     * @param deleteCommentReqVO 删除评论请求参数
     * @return 删除评论响应结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long commentId = deleteCommentReqVO.getId();

        // 查询该评论是一级评论，还是二级评论
        CommentDO commentDO = commentMapper.selectOneById(commentId);

        // 判断评论是否存在
        if (Objects.isNull(commentDO)) {
            log.warn("该评论不存在, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // 删除评论
        commentMapper.deleteById(commentId);

        Long replayCommentId = commentDO.getReplyCommentId();

        // 一级评论
        if (Objects.isNull(replayCommentId)) {
            // 删除子评论
            commentMapper.deleteByParentCommentId(commentId);
        } else { // 二级评论
            // 删除此评论, 以及此评论下的所有回复
            deleteAllChildComment(commentId);
        }

        return Response.success();
    }

    /**
     * 批量删除所有子评论
     *
     * @param commentId 评论 ID
     */
    private void deleteAllChildComment(Long commentId) {
        // 查询此评论的所有回复
        List<CommentDO> childCommentDOS = commentMapper.selectByReplyCommentId(commentId);

        if (CollectionUtils.isEmpty(childCommentDOS))
            return;

        // 批量删除
        commentMapper.deleteBatchByIds(childCommentDOS.stream().map(CommentDO::getId).collect(Collectors.toList()));
    }

    /**
     * 评论审核
     *
     * @param examineCommentReqVO 评论审核请求参数
     * @return 评论审核响应结果
     */
    @Override
    public Response<?> examine(ExamineCommentReqVO examineCommentReqVO) {
        Long commentId = examineCommentReqVO.getId();
        Integer status = examineCommentReqVO.getStatus();
        String reason = examineCommentReqVO.getReason();

        // 根据提交的评论 ID 查询该条评论
        CommentDO commentDO = commentMapper.selectOneById(commentId);

        // 判空
        if (Objects.isNull(commentDO)) {
            log.warn("该评论不存在, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // 评论当前状态
        Integer currStatus = commentDO.getStatus();

        // 若未处于待审核状态
        if (!Objects.equals(currStatus, CommentStatusEnum.WAIT_EXAMINE.getCode())) {
            log.warn("该评论未处于待审核状态, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_STATUS_NOT_WAIT_EXAMINE);
        }

        // 更新评论
        commentMapper.update(CommentDO.builder()
                .id(commentId)
                .status(status)
                .reason(reason)
                .build(), true);

        return Response.success();
    }
}

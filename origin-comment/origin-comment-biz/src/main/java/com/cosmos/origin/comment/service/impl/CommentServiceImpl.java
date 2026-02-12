package com.cosmos.origin.comment.service.impl;

import com.cosmos.origin.comment.domain.dos.CommentDO;
import com.cosmos.origin.comment.domain.mapper.CommentMapper;
import com.cosmos.origin.comment.enums.CommentStatusEnum;
import com.cosmos.origin.comment.model.vo.FindCommentItemRspVO;
import com.cosmos.origin.comment.model.vo.FindCommentListReqVO;
import com.cosmos.origin.comment.model.vo.FindCommentListRspVO;
import com.cosmos.origin.comment.model.vo.PublishCommentReqVO;
import com.cosmos.origin.comment.service.CommentService;
import com.cosmos.origin.common.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import toolgood.words.WordsSearch;
import toolgood.words.WordsSearchResult;

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
     * 查询评论列表数据
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
}

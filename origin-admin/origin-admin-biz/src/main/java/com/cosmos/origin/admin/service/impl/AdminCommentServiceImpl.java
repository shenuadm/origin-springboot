package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.model.vo.comment.DeleteCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.ExamineCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.FindCommentPageListReqVO;
import com.cosmos.origin.admin.model.vo.comment.FindCommentPageListRspVO;
import com.cosmos.origin.admin.service.AdminCommentService;
import com.cosmos.origin.comment.domain.dos.CommentDO;
import com.cosmos.origin.comment.domain.mapper.CommentMapper;
import com.cosmos.origin.comment.enums.CommentStatusEnum;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentMapper commentMapper;

    /**
     * 查询评论分页数据
     *
     * @param findCommentPageListReqVO 查询评论分页数据请求参数
     * @return 查询评论分页数据响应结果
     */
    @Override
    public PageResponse<?> findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO) {
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
    public Response<?> examineComment(ExamineCommentReqVO examineCommentReqVO) {
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

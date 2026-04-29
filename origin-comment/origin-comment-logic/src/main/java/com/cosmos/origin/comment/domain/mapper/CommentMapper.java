package com.cosmos.origin.comment.domain.mapper;

import com.cosmos.origin.comment.domain.dos.CommentDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface CommentMapper extends BaseMapper<CommentDO> {

    /**
     * 根据路由地址、状态查询对应的评论
     *
     * @param routerUrl 路由地址
     * @return 评论列表
     */
    default List<CommentDO> selectByRouterUrlAndStatus(String routerUrl, Integer status) {
        return selectListByQuery(QueryWrapper.create()
                .eq(CommentDO::getRouterUrl, routerUrl) // 按路由地址查询
                .eq(CommentDO::getStatus, status) // 按状态查询
                .orderBy(CommentDO::getCreateTime, false) // 按创建时间倒序
        );
    }

    /**
     * 分页查询
     *
     * @param current   当前页码
     * @param size      每页数量
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 评论列表
     */
    default Page<CommentDO> selectPageList(Long current, Long size, String routerUrl,
                                           LocalDate startDate, LocalDate endDate, Integer status) {
        // 分页对象(查询第几页、每页多少数据)
        Page<CommentDO> page = new Page<>(current, size);

        // 构建查询条件
        QueryWrapper wrapper = QueryWrapper.create()
                .like(CommentDO::getRouterUrl, routerUrl, StringUtils.isNotBlank(routerUrl)) // like 模糊查询
                .eq(CommentDO::getStatus, status, Objects.nonNull(status)) // 评论状态
                .ge(CommentDO::getCreateTime, startDate, Objects.nonNull(startDate)) // 大于等于 startDate
                .le(CommentDO::getCreateTime, endDate, Objects.nonNull(endDate))  // 小于等于 endDate
                .orderBy(CommentDO::getCreateTime, false); // 按创建时间倒叙

        return paginate(page, wrapper);
    }

    /**
     * 根据 reply_comment_id 查询评论
     *
     * @param replyCommentId 回复评论 ID
     * @return 评论列表
     */
    default List<CommentDO> selectByReplyCommentId(Long replyCommentId) {
        return selectListByQuery(QueryWrapper.create()
                .eq(CommentDO::getReplyCommentId, replyCommentId)
                .orderBy(CommentDO::getCreateTime, false)
        );
    }

    /**
     * 根据 parent_comment_id 删除
     *
     * @param id 父评论 ID
     * @return 删除结果
     */
    default int deleteByParentCommentId(Long id) {
        return deleteByQuery(QueryWrapper.create()
                .eq(CommentDO::getParentCommentId, id));
    }
}

package com.cosmos.origin.websocket.domain.mapper;

import com.cosmos.origin.websocket.domain.dos.ChatMessageDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;
import java.util.Objects;

/**
 * 聊天室消息
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessageDO> {

    /**
     * 查询最近的消息记录（查询第一页）
     *
     * @param limit 条数
     * @return 聊天消息
     */
    default List<ChatMessageDO> selectRecentMessages(int limit) {
        return selectListByQuery(QueryWrapper.create()
                .orderBy(ChatMessageDO::getCreateTime, false) // 按创建时间倒序
                .limit(limit)); // 添加 limit
    }

    /**
     * 分页查询历史消息（向前翻页）
     * PS: 游标分页，防止深度分页问题
     *
     * @param lastId 上一条消息的 ID
     * @param limit  条数
     * @return 聊天消息
     */
    default List<ChatMessageDO> selectMessagesBefore(Long lastId, int limit) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (Objects.nonNull(lastId)) {
            queryWrapper.lt(ChatMessageDO::getId, lastId); // 过滤出小于 lastId 的记录
        }
        return selectListByQuery(queryWrapper
                .orderBy(ChatMessageDO::getCreateTime, false) // 按创建时间倒序
                .limit(limit)); // 添加 limit
    }
}

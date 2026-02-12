package com.cosmos.origin.websocket.config;

import com.cosmos.origin.common.spi.WebSocketUserServiceProvider;
import com.cosmos.origin.common.utils.JsonUtil;
import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import com.cosmos.origin.websocket.domain.dos.ChatMessageDO;
import com.cosmos.origin.websocket.domain.mapper.ChatMessageMapper;
import com.cosmos.origin.websocket.enums.ChatRoomMessageTypeEnum;
import com.cosmos.origin.websocket.model.vo.chatroom.ChatMessageVO;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUsersMessageVO;
import com.cosmos.origin.websocket.model.vo.chatroom.UserInfoVO;
import com.cosmos.origin.websocket.utils.SpringContext;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 公共聊天室 WebSocket 服务端
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Slf4j
@ServerEndpoint("/ws/chat") // 定义 WebSocket 端点
public class ChatWebSocketServer {

    /**
     * 存储所有在线用户的会话 Session
     */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 在线人数
     */
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    /**
     * 存储用户信息
     */
    private static final Map<String, UserInfoVO> USER_INFO_MAP = new ConcurrentHashMap<>();

    /**
     * 时间格式化
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        // 获取会话 ID
        String sessionId = session.getId();
        // 保存会话，使用会话 ID 作为 Key
        SESSION_MAP.put(sessionId, session);

        // 获取 Token，解析用户名
        Map<String, List<String>> params = session.getRequestParameterMap();
        String token = getFieldValueFromParam("token", params);
        JwtTokenHelper jwtTokenHelper = SpringContext.getBean(JwtTokenHelper.class);
        String username = jwtTokenHelper.getUsernameByToken(token);

        // 从用户服务获取用户信息（通过SPI接口，由业务模块实现）
        String nickname = username;
        String avatar = null;
        try {
            WebSocketUserServiceProvider userService = SpringContext.getBean(WebSocketUserServiceProvider.class);
            Optional<WebSocketUserServiceProvider.UserInfo> userInfoOpt = userService.findByUsername(username);
            if (userInfoOpt.isPresent()) {
                WebSocketUserServiceProvider.UserInfo userInfo = userInfoOpt.get();
                nickname = userInfo.getNickname();
                avatar = userInfo.getAvatar();
            }
        } catch (Exception e) {
            // 如果服务未实现或发生异常，使用用户名作为昵称
            log.debug("WebSocketUserServiceProvider not available or error, using username as nickname");
        }

        // 保存用户信息
        USER_INFO_MAP.put(sessionId, new UserInfoVO(username, nickname, avatar));

        // 在线人数+1
        int count = ONLINE_COUNT.incrementAndGet();

        log.info("## 用户 [sessionId:{}] [昵称:{}] 加入聊天室，当前在线人数: {}", sessionId, nickname, count);

        // 广播系统消息，告诉所有人，有用户加入了聊天室
        broadcastMessage(buildMessage(ChatRoomMessageTypeEnum.SYSTEM.getCode(), nickname, null, "加入了聊天室"));
        // 广播在线用户列表
        broadcastOnlineUsers();
    }

    /**
     * 获取对应参数
     *
     * @param fieldName 字段名
     * @param params    参数
     * @return 对应参数的值
     */
    private String getFieldValueFromParam(String fieldName, Map<String, List<String>> params) {
        // 如果包含对应字段，且不为空，则取出
        if (params.containsKey(fieldName)
                && !CollectionUtils.isEmpty(params.get(fieldName))) {
            return params.get(fieldName).get(0);
        }
        return null;
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 获取会话 ID
        String sessionId = session.getId();
        // 获取对应用户信息
        UserInfoVO userInfoVO = USER_INFO_MAP.get(sessionId);

        // 校验用户信息是否存在
        if (Objects.isNull(userInfoVO)) {
            log.warn("## 用户信息不存在，[sessionId:{}]", sessionId);
            return;
        }

        String username = userInfoVO.getUsername();
        String nickname = userInfoVO.getNickname();
        String avatar = userInfoVO.getAvatar();

        log.info("## 收到用户 [sessionId:{}] [昵称:{}] 的消息: {}", sessionId, nickname, message);

        // 保存消息到数据库
        saveMessage(username, nickname, avatar, message);

        // 广播聊天消息，通知所有在线用户
        broadcastMessage(buildMessage(ChatRoomMessageTypeEnum.CHAT.getCode(), nickname, avatar, message));
    }

    /**
     * 保存消息到数据库
     */
    private void saveMessage(String username, String nickname, String avatar, String content) {
        ChatMessageDO chatMessage = ChatMessageDO.builder()
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .content(content)
                .createTime(LocalDateTime.now())
                .build();
        ChatMessageMapper chatMessageMapper = SpringContext.getBean(ChatMessageMapper.class);
        chatMessageMapper.insert(chatMessage);
    }

    /**
     * 构建消息 JSON 字符串
     */
    private String buildMessage(Integer type, String nickname, String avatar, String content) {
        return JsonUtil.toJsonString(ChatMessageVO.builder()
                .type(type)
                .nickname(nickname)
                .avatar(avatar)
                .content(content)
                .time(LocalDateTime.now().format(TIME_FORMATTER))
                .onlineCount(ONLINE_COUNT.get())
                .build());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();

        // 如果会话存在
        if (SESSION_MAP.containsKey(sessionId)) {
            // 获取用户信息
            UserInfoVO userInfo = USER_INFO_MAP.get(sessionId);
            String nickname = Objects.nonNull(userInfo) ? userInfo.getNickname() : null;

            // 删除对应会话
            SESSION_MAP.remove(sessionId);
            // 删除用户信息
            USER_INFO_MAP.remove(sessionId);

            // 在线人数-1
            int count = ONLINE_COUNT.decrementAndGet();

            log.info("## 用户 [sessionId:{}] [昵称:{}] 离开了聊天室，当前在线人数: {}", sessionId, nickname, count);

            // 广播系统消息，告诉所有在线用户，有人离开了
            broadcastMessage(buildMessage(ChatRoomMessageTypeEnum.SYSTEM.getCode(), nickname, null, "离开了聊天室"));
            // 广播在线用户列表
            broadcastOnlineUsers();
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("## WebSocket 连接发生错误：", error);
    }

    /**
     * 发送消息给指定客户端
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("## 发送消息失败：", e);
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    private void broadcastMessage(String message) {
        SESSION_MAP.values().forEach(session -> sendMessage(session, message));
    }

    /**
     * 广播在线用户列表
     */
    private void broadcastOnlineUsers() {
        // 构建在线用户列表 VO
        OnlineUsersMessageVO msg = OnlineUsersMessageVO.builder()
                .type(ChatRoomMessageTypeEnum.ONLINE_USERS.getCode())
                .onlineCount(ONLINE_COUNT.get())
                .users(getOnlineUsers())
                .build();

        // 广播消息
        broadcastMessage(JsonUtil.toJsonString(msg));
    }

    /**
     * 获取在线用户列表
     */
    public static List<OnlineUserVO> getOnlineUsers() {
        List<OnlineUserVO> users = new ArrayList<>();
        USER_INFO_MAP.forEach((sessionId, userInfo) -> users.add(OnlineUserVO.builder()
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .online(true)
                .build()));
        return users;
    }
}

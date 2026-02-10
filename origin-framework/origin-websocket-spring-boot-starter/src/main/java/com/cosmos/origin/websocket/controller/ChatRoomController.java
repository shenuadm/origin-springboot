package com.cosmos.origin.websocket.controller;

import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListReqVO;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListRspVO;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;
import com.cosmos.origin.websocket.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
@Tag(name = "聊天室")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/message/history")
    @Operation(summary = "获取历史消息")
    @ApiOperationLog(description = "获取历史消息")
    public Response<FindChatMessagePageListRspVO> findHistoryMessages(@RequestBody @Validated FindChatMessagePageListReqVO findChatMessagePageListReqVO) {
        return chatRoomService.findHistoryMessages(findChatMessagePageListReqVO);
    }

    @PostMapping("/online/users")
    @Operation(summary = "获取所有在线用户")
    @ApiOperationLog(description = "获取所有在线用户")
    public Response<List<OnlineUserVO>> findOnlineUsers() {
        return chatRoomService.findOnlineUsers();
    }
}

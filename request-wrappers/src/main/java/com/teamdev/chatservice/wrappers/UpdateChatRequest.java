package com.teamdev.chatservice.wrappers;

import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

public class UpdateChatRequest {

    public final Token token;
    public final UserId userId;
    public final ChatRoomId chatRoomId;

    public UpdateChatRequest(Token token, UserId userId, ChatRoomId chatRoomId) {
        this.token = token;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }
}

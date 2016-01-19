package com.teamdev.chatservice.wrappers;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

public class ChatRoomRequest {

    public final Token token;
    public final UserId userId;
    public final String name;

    public ChatRoomRequest(Token token, UserId userId, String name) {
        this.token = token;
        this.userId = userId;
        this.name = name;
    }
}

package com.teamdev.chatservice.wrappers;

import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;

public class MessageRequest {

    public final Token token;
    public final UserId userId;
    public final long receiverId;
    public final String text;

    public MessageRequest(Token token, UserId userId, long receiverId, String text) {
        this.token = token;
        this.userId = userId;
        this.receiverId = receiverId;
        this.text = text;
    }
}

package com.teamdev.chatservice.wrappers;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

import java.time.LocalDateTime;

public class ReadMessagesRequest {

    public final Token token;
    public final UserId userId;
    public final LocalDateTime dateTime;

    public ReadMessagesRequest(Token token, UserId userId, LocalDateTime dateTime) {
        this.token = token;
        this.userId = userId;
        this.dateTime = dateTime;
    }
}

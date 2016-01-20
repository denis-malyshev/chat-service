package com.teamdev.chatservice.wrappers;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

import java.util.Date;

public class ReadMessagesRequest {

    public final Token token;
    public final UserId userId;
    public final Date date;

    public ReadMessagesRequest(Token token, UserId userId, Date date) {
        this.token = token;
        this.userId = userId;
        this.date = date;
    }
}

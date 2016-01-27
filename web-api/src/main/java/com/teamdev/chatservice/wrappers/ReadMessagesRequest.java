package com.teamdev.chatservice.wrappers;

import com.teamdev.chatservice.wrappers.dto.ChatRoomId;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;

import java.util.Date;

public class ReadMessagesRequest {

    public final Token token;
    public final UserId userId;
    public final Date date;

    public ChatRoomId chatRoomId;

    public ReadMessagesRequest(Token token, UserId userId, Date date) {
        this.token = token;
        this.userId = userId;
        this.date = date;
    }

    public ReadMessagesRequest(Token token, UserId userId, Date date, ChatRoomId chatRoomId) {
        this.token = token;
        this.userId = userId;
        this.date = date;
        this.chatRoomId = chatRoomId;
    }
}

package com.teamdev.web.requset;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

public class TokenRequest {

    public final Token token;
    public final UserId userId;

    public TokenRequest(Token token, UserId userId) {
        this.token = token;
        this.userId = userId;
    }
}

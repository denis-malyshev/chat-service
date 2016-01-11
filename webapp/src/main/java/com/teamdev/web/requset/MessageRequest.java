package com.teamdev.web.requset;

public class MessageRequest {

    public final TokenRequest tokenRequest;
    public final long receiverId;
    public final String text;

    public MessageRequest(TokenRequest tokenRequest, long receiverId, String text) {
        this.tokenRequest = tokenRequest;
        this.receiverId = receiverId;
        this.text = text;
    }
}

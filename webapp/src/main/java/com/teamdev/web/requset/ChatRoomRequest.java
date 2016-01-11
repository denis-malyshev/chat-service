package com.teamdev.web.requset;

public class ChatRoomRequest {

    public final String name;
    public final TokenRequest tokenRequest;

    public ChatRoomRequest(String name, TokenRequest tokenRequest) {
        this.name = name;
        this.tokenRequest = tokenRequest;
    }
}

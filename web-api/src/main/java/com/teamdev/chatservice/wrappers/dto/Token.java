package com.teamdev.chatservice.wrappers.dto;

public class Token  {

    public String key;
    public long userId;

    public Token(String key) {
        this.key = key;
    }

    public Token(String key, long userId) {
        this.key = key;
        this.userId = userId;
    }
}

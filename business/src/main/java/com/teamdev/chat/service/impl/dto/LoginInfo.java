package com.teamdev.chat.service.impl.dto;

public class LoginInfo {

    public final String email;
    public final String password;

    public LoginInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

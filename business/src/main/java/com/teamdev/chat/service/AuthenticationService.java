package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;

public interface AuthenticationService {

    Token login(UserEmail email, UserPassword password) throws AuthenticationException;

    void logout(Token token);

    void validation(Token token, UserId userId) throws AuthenticationException;
}

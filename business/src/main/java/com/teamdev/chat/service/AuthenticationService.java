package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.AuthenticationException;

public interface AuthenticationService {

    Token login(LoginInfo loginInfo) throws AuthenticationException;

    void logout(Token token);

    void validate(Token token, UserId userId) throws AuthenticationException;
}

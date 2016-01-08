package com.teamdev.chat.service.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.teamdev.utils.ToolsProvider.passwordHash;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = Logger.getLogger(AuthenticationServiceImpl.class);
    @Autowired
    private AuthenticationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    private HashFunction hashFunction = Hashing.md5();

    public AuthenticationServiceImpl() {
    }

    public Token login(UserEmail userEmail, UserPassword password) throws AuthenticationException {

        User user = userRepository.findByMail(userEmail.email);

        String passwordHash = passwordHash(password.password);

        if (user == null || !user.getPassword().equals(passwordHash)) {
            throw new AuthenticationException("Invalid login or password.");
        }

        AuthenticationToken token = generateToken(user.getId());
        user.setToken(token.getKey());
        return new Token(token.getKey());
    }

    @Override
    public void logout(Token token) {
        AuthenticationToken innerToken = tokenRepository.findByKey(token.key);
        if (innerToken != null) {
            tokenRepository.delete(innerToken.getId());
        }
    }

    @Override
    public void validate(Token token, UserId userId) throws AuthenticationException {

        AuthenticationToken innerToken = tokenRepository.findByKey(token.key);

        if (innerToken == null || innerToken.getUserId() != userId.id) {
            throw new AuthenticationException("Invalid token.");
        }
        if (innerToken.getExpirationTime().compareTo(LocalDateTime.now()) < 1) {
            throw new AuthenticationException("Token has been expired.");
        }
    }

    private AuthenticationToken generateToken(long userId) {
        AuthenticationToken token = new AuthenticationToken(userId);
        tokenRepository.update(token);
        return token;
    }
}

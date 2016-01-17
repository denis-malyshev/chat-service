package com.teamdev.chat.service.impl;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.teamdev.utils.Hasher.createHash;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = Logger.getLogger(AuthenticationServiceImpl.class);
    @Autowired
    private AuthenticationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    public AuthenticationServiceImpl() {
    }

    public Token login(LoginInfo loginInfo) throws AuthenticationException {
        LOG.info(String.format("User %s trying to login.", loginInfo.email));
        User user = userRepository.findByEmail(loginInfo.email);

        String passwordHash = createHash(loginInfo.password);

        if (user == null || !user.getPassword().equals(passwordHash)) {
            throw new AuthenticationException("Invalid login or password.");
        }

        AuthenticationToken token = generateToken(user.getId());
        user.setToken(token);
        LOG.info(String.format("User %s logged successfully.", loginInfo.email));
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
        LOG.info("Checking user token.");
        AuthenticationToken innerToken = tokenRepository.findByKey(token.key);

        if (innerToken == null || innerToken.getUserId() != userId.id) {
            LOG.error("Invalid token.");
            throw new AuthenticationException("Invalid token.");
        }
        if (innerToken.getExpirationTime().compareTo(LocalDateTime.now()) < 1) {
            LOG.error("Token has been expired.");
            throw new AuthenticationException("Token has been expired.");
        }

        LOG.info("User's token is valid.");
    }

    private AuthenticationToken generateToken(long userId) {
        AuthenticationToken token = new AuthenticationToken(userId);
        tokenRepository.save(token);
        return token;
    }
}

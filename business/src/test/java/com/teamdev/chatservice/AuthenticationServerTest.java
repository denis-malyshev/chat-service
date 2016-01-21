package com.teamdev.chatservice;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chatservice.wrappers.dto.LoginInfo;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.teamdev.utils.Hasher.createHash;
import static org.junit.Assert.*;

public class AuthenticationServerTest extends SpringContextRunner {

    @Autowired
    private AuthenticationService tokenService;
    @Autowired
    private AuthenticationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_login_user() {
        userRepository.save(new User("Vasya", "vasya.auth.service@gmail.com", createHash("pwd")));

        try {
            Token token = tokenService.login(new LoginInfo("vasya.auth.service@gmail.com", "pwd"));
            assertNotNull("Token must exists.", token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test_login_user_with_invalid_email() {

        try {
            tokenService.login(new LoginInfo("invalid@gmail.com", "pwd"));

            fail();
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "Invalid login or password.", result);
        }
    }

    @Test
    public void test_logout() {
        userRepository.save(new User("Vasya", "vasya@gmail.com", createHash("pwd")));

        try {
            Token token = tokenService.login(new LoginInfo("vasya@gmail.com", "pwd"));
            tokenService.logout(token);
            AuthenticationToken result = tokenRepository.findByTokenKey(token.key);
            assertNull("The token should not exist.",result);
        } catch (AuthenticationException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}

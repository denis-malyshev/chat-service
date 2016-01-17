package com.teamdev.chatservice;

import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.teamdev.utils.Hasher.createHash;
import static org.junit.Assert.*;

public class AuthenticationServerTest {

    private AuthenticationService tokenService;
    private UserRepository userRepository;

    @Before
    public void setUp() {

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        tokenService = context.getBean(AuthenticationService.class);
        userRepository = context.getBean(UserRepository.class);
    }

    @Test
    public void testLoginUser() {
        userRepository.save(new User("Vasya", "vasya.auth.service@gmail.com", createHash("pwd")));

        try {
            Token token = tokenService.login(new LoginInfo("vasya.auth.service@gmail.com", "pwd"));
            assertNotNull("Token must exists.", token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testLoginUserWithInvalidEmail() {

        try {
            tokenService.login(new LoginInfo("invalid@gmail.com", "pwd"));
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "Invalid login or password.", result);
        }
    }

    @Test
    public void testLogout() {
        userRepository.save(new User("Vasya", "vasya@gmail.com", createHash("pwd")));

        try {
            Token token = tokenService.login(new LoginInfo("vasya@gmail.com", "pwd"));
            tokenService.logout(token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        }
    }
}

package com.teamdev.chatservice;

import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.application.ApplicationConfig;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.teamdev.utils.JsonHelper.passwordHash;
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
        userRepository.update(new User("Vasya", "vasya@gmail.com", passwordHash("pwd")));

        try {
            Token token = tokenService.login(new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
            assertNotNull("Token must exists.", token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testLoginUserWithInvalidEmail() {

        try {
            tokenService.login(new UserEmail("invalid@gmail.com"), new UserPassword("pwd"));
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "Invalid login or password.", result);
        }
    }

    @Test
    public void testLogout() {
        userRepository.update(new User("Vasya", "vasya@gmail.com", passwordHash("pwd")));

        try {
            Token token = tokenService.login(new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
            tokenService.logout(token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        }
    }
}

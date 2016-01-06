package com.teamdev.chatservice;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.application.ApplicationConfig;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AuthenticationServerTest {

    private AuthenticationService tokenService;
    private UserRepository userRepository;

    private HashFunction hashFunction = Hashing.md5();


    @Before
    public void setUp() throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        tokenService = context.getBean(AuthenticationService.class);
        userRepository = context.getBean(UserRepository.class);
    }

    @Test
    public void testLoginUser() throws Exception {

        String passwordHash = hashFunction.newHasher().putString("pwd", Charset.defaultCharset()).hash().toString();

        userRepository.update(new User("Vasya", "vasya@gmail.com", passwordHash));

        Token token = tokenService.login(new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
        assertNotNull("Token must exists.", token);
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
    public void testLogout() throws Exception {

        String passwordHash = hashFunction.newHasher().putString("pwd", Charset.defaultCharset()).hash().toString();

        userRepository.update(new User("Vasya", "vasya@gmail.com", passwordHash));

        try {
            Token token = tokenService.login(new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
            tokenService.logout(token);
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        }
    }
}

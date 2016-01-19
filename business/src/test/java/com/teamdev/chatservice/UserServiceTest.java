package com.teamdev.chatservice;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    private AuthenticationTokenRepository tokenRepository;

    private User user1;
    private UserDTO userDTO = new UserDTO("Vasya", "vasya.user.service@gmail.com", "pwd");

    @Before
    public void setUp() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        userService = context.getBean(UserService.class);
        userRepository = context.getBean(UserRepository.class);
        user1 = new User("Vasya", "vasya.user.service@gmail.com", "pwd1");

        tokenRepository = context.getBean(AuthenticationTokenRepository.class);
    }

    @Test
    public void testRegistrationUser() {
        try {
            UserDTO register = userService.register(new UserDTO("Vasya", "vasya.use1r.service@gmail.com", "pwd"));

            assertNotNull("User must be exist.",register);
        } catch (AuthenticationException | RegistrationException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testRegistrationUserWithExistingEmail() {
        userRepository.save(user1);

        try {
            userService.register(userDTO);

            fail();
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "User with the same mail already exists.", result);
        } catch (RegistrationException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testRegistrationUserWithIncorrectEmail() {
        try {

            UserDTO invalidUserDTO = new UserDTO("Vasya", "vasya-gmail.com", "pwd");

            userService.register(invalidUserDTO);

            fail();
        } catch (AuthenticationException e) {
            fail("Unexpected exception.");
        } catch (RegistrationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "Enter a correct email.", result);
        }
    }

    @Test
    public void testFindUserById() throws UserNotFoundException {
        userRepository.save(user1);
        AuthenticationToken authenticationToken = new AuthenticationToken(user1);
        tokenRepository.save(authenticationToken);

        UserDTO userDTO = userService.findById(new Token(authenticationToken.getKey()), new UserId(user1.getId()), new UserId(user1.getId()));
        assertNotNull("UserDTO must exist.", userDTO);
    }
}

package com.teamdev.chatservice;

import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.application.ApplicationConfig;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.dto.UserName;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    private User user1;

    @Before
    public void setUp() throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        userService = context.getBean(UserService.class);
        userRepository = context.getBean(UserRepository.class);
        user1 = new User("Vasya", "vasya@gmail.com", "pwd1");
    }

    @Test
    public void testRegistrationUser() throws Exception {

        userService.register(
                new UserName(user1.getFirstName()),
                new UserEmail(user1.getEmail()),
                new UserPassword(user1.getPassword()));

        int result = userRepository.userCount();
        assertEquals("User must be exist.", 1, result);
    }

    @Test
    public void testRegistrationUserWithExistingEmail() {

        userRepository.update(user1);

        try {

            userService.register(
                    new UserName(user1.getFirstName()),
                    new UserEmail(user1.getEmail()),
                    new UserPassword(user1.getPassword()));

            fail();
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "User with the same mail already exists.", result);
        }
    }

    @Test
    public void testFindUserById() throws Exception {

        userRepository.update(user1);

        UserDTO userDTO = userService.findById(new UserId(user1.getId()));
        assertNotNull("UserDTO must be exist.", userDTO);
    }
}

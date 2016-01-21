package com.teamdev.chatservice;

import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.service.UserService;
import com.teamdev.chatservice.wrappers.dto.UserDTO;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserServiceTest extends SpringContextRunner {

    private static final Logger LOG = Logger.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private UserDTO testUserDTO = new UserDTO("Vasya", "vasya.user.service@gmail.com", "pwd");

    @Test
    public void test_registration_user() {
        try {
            UserDTO register = userService.register(new UserDTO("Vasya", "vasya.use1r.service@gmail.com", "pwd"));

            assertNotNull("User must be exist.", register);
        } catch (AuthenticationException | RegistrationException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test_registration_user_with_existing_email() {
        try {
            userService.register(testUserDTO);
            userService.register(testUserDTO);

            fail();
        } catch (AuthenticationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "User with the same mail already exists.", result);
        } catch (RegistrationException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test_registration_user_with_incorrect_email() {
        try {
            UserDTO invalidUserDTO = new UserDTO("Vasya", "vasya-gmail.com", "pwd");
            userService.register(invalidUserDTO);

            fail();
        } catch (AuthenticationException e) {
            fail("Unexpected exception: " + e.getMessage());
        } catch (RegistrationException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "Enter a correct email.", result);
        }
    }
}

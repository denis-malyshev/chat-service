package com.teamdev.chatservice;

import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chatservice.wrappers.dto.UserDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserServiceTest extends SpringContextRunner {

    @Autowired
    private UserService userService;
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

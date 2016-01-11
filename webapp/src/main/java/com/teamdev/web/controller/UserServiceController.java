package com.teamdev.web.controller;

import com.teamdev.chat.service.impl.UserServiceImpl;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserName;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/user")
@Controller
public final class UserServiceController {

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(value = "/users/{name}", method = RequestMethod.GET)
    @ResponseBody
    public UserDTO getUserInfo(@PathVariable String name) throws UserNotFoundException, AuthenticationException, RegistrationException {
        return userService.register(new UserName(name), new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
    }

}

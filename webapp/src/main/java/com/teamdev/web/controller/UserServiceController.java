package com.teamdev.web.controller;

import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserName;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@Controller
public final class UserServiceController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/create/{name}/{email}/{password}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO)
            throws AuthenticationException, RegistrationException {
        UserDTO userDTOreg = userService.register(userDTO);
        return new ResponseEntity<>(userDTOreg, HttpStatus.OK);
    }

}

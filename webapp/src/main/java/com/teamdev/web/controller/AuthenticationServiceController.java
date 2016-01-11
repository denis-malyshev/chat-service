package com.teamdev.web.controller;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticationServiceController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login/{email}/{password}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Token> login(@PathVariable String email, @PathVariable String password)
            throws AuthenticationException {
        Token token = authenticationService.login(new UserEmail(email), new UserPassword(password));
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}

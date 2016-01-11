package com.teamdev.web.controller;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public final class AuthenticationServiceController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Token> login(@RequestBody LoginInfo loginInfo)
            throws AuthenticationException {
        Token token = authenticationService.login(loginInfo);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public String logout(@RequestBody Token token)
            throws AuthenticationException {
        authenticationService.logout(token);
        return "successfully";
    }
}

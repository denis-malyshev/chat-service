package com.teamdev.web.controller;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chatservice.wrappers.dto.LoginInfo;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@Controller
public final class AuthenticationServiceController {

    private static final Logger LOG = Logger.getLogger(AuthenticationServiceController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Token> login(@RequestBody LoginInfo loginInfo) {
        try {
            Token token = authenticationService.login(loginInfo);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/logout", params = "token", method = RequestMethod.DELETE)
    @ResponseBody
    public String logout(@RequestParam String token) {
        authenticationService.logout(new Token(token));
        return "successfully";
    }
}

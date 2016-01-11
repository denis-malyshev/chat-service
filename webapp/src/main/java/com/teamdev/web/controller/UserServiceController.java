package com.teamdev.web.controller;

import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.web.requset.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/user")
@Controller
public final class UserServiceController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO)
            throws AuthenticationException, RegistrationException {
        return new ResponseEntity<>(userService.register(userDTO), HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{userId}", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserDTO> findById(@RequestParam String token, @RequestParam long searcherId, @PathVariable long userId)
            throws AuthenticationException, RegistrationException, UserNotFoundException {
        return new ResponseEntity<>(userService.findById(
                new Token(token),
                new UserId(searcherId),
                new UserId(userId)), HttpStatus.OK);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDTO>> findAvailableChats(@RequestBody TokenRequest tokenRequest)
            throws AuthenticationException, RegistrationException, UserNotFoundException {
        return new ResponseEntity<>(userService.findAvailableChats(tokenRequest.token, tokenRequest.userId), HttpStatus.OK);
    }

}

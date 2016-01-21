package com.teamdev.web.controller;

import com.teamdev.chat.service.UserService;
import com.teamdev.chatservice.wrappers.dto.ChatRoomDTO;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserDTO;
import com.teamdev.chatservice.wrappers.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/user")
@Controller
public final class UserServiceController {

    private static final Logger LOG = Logger.getLogger(UserServiceController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO)
            throws AuthenticationException, RegistrationException {
        try {
            return new ResponseEntity<>(userService.register(userDTO), HttpStatus.OK);
        } catch (AuthenticationException | RegistrationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find/{id}", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserDTO> findById(@RequestParam String token, @RequestParam long userId, @PathVariable long id) throws UserNotFoundException {
        try {
            return new ResponseEntity<>(userService.findById(
                    new Token(token),
                    new UserId(userId),
                    new UserId(id)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/chats", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDTO>> findAvailableChats(@RequestParam String token, @RequestParam long userId) {
        return new ResponseEntity<>(userService.findAvailableChats(new Token(token), new UserId(userId)), HttpStatus.OK);
    }

}
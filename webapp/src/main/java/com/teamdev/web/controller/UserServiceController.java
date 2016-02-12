package com.teamdev.web.controller;

import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chatservice.wrappers.dto.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
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
                    new Token(token, userId),
                    new UserId(userId),
                    new UserId(id)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find_by_chat/{id}", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<UserDTO>> findByChatRoomId(@RequestParam String token, @RequestParam long userId, @PathVariable long id) {
        return new ResponseEntity<>(userService.findUsersByChatRoomId(
                new Token(token, userId),
                new UserId(userId),
                new ChatRoomId(id)), HttpStatus.OK);
    }

    @RequestMapping(value = "/chats", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<ChatRoomDTO>> findAvailableChats(@RequestParam String token, @RequestParam long userId) {
        return new ResponseEntity<>(userService.findAvailableChats(new Token(token, userId), new UserId(userId)), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", params = {"token", "userId"}, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> delete(@RequestParam String token, @RequestParam long userId)
            throws UserNotFoundException {
        try {
            return new ResponseEntity<>(userService.delete(new Token(token, userId), new UserId(userId)), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

}
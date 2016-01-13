package com.teamdev.web.controller;

import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.web.wrappers.ChatRoomRequest;
import com.teamdev.web.wrappers.UpdateChatRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/chat")
@Controller
public final class ChatServiceController {

    private static final Logger LOG = Logger.getLogger(ChatServiceController.class);

    @Autowired
    private ChatRoomService chatRoomService;

    @RequestMapping(value = "/chats/all", params = {"token", "userId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDTO>> readAll(@RequestParam String token, @RequestParam long userId) {
        return new ResponseEntity<>(chatRoomService.findAll(new Token(token), new UserId(userId)), HttpStatus.OK);
    }

    @RequestMapping(value = "/join", method = RequestMethod.PUT)
    @ResponseBody
    public String joinUserToChat(@RequestBody UpdateChatRequest updateChatRequest)
            throws UserNotFoundException, ChatRoomNotFoundException, AuthenticationException {
        try {
            chatRoomService.joinToChatRoom(
                    updateChatRequest.token,
                    updateChatRequest.userId,
                    updateChatRequest.chatRoomId);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
        return "User successfully joined to chat.";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.PUT)
    @ResponseBody
    public String deleteUserFromChat(@RequestBody UpdateChatRequest updateChatRequest)
            throws ChatRoomNotFoundException, UserNotFoundException, AuthenticationException {
        try {
            chatRoomService.leaveChatRoom(
                    updateChatRequest.token,
                    updateChatRequest.userId,
                    updateChatRequest.chatRoomId);
        } catch (AuthenticationException | ChatRoomNotFoundException | UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
        return "User successfully deleted from chat.";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChatRoomDTO> createChat(@RequestBody ChatRoomRequest roomRequest)
            throws ChatRoomAlreadyExistsException {
        try {
            return new ResponseEntity<>(chatRoomService.create(
                    roomRequest.token,
                    roomRequest.userId,
                    roomRequest.name), HttpStatus.OK);
        } catch (ChatRoomAlreadyExistsException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }


}

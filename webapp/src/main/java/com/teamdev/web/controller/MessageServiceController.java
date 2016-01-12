package com.teamdev.web.controller;

import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.MessageDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.web.requset.MessageRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/message")
@Controller
public class MessageServiceController {

    private static final Logger LOG = Logger.getLogger(MessageServiceController.class);

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageRequest messageRequest) {
        Token token = messageRequest.token;
        UserId userId = messageRequest.userId;
        ChatRoomId chatRoomId = new ChatRoomId(messageRequest.receiverId);
        try {
            return new ResponseEntity<>(messageService.sendMessage(
                    token,
                    userId,
                    chatRoomId,
                    messageRequest.text), HttpStatus.OK);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/send_private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendPrivateMessage(@RequestBody MessageRequest messageRequest) {
        Token token = messageRequest.token;
        UserId userId = messageRequest.userId;
        UserId receiverId = new UserId(messageRequest.receiverId);
        try {
            return new ResponseEntity<>(messageService.sendPrivateMessage(
                    token,
                    userId,
                    receiverId,
                    messageRequest.text), HttpStatus.OK);
        } catch (AuthenticationException | UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
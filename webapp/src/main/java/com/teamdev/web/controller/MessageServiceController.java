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

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageRequest messageRequest)
            throws UserNotFoundException, ChatRoomNotFoundException, AuthenticationException {
        Token token = messageRequest.tokenRequest.token;
        UserId userId = messageRequest.tokenRequest.userId;
        ChatRoomId chatRoomId = new ChatRoomId(messageRequest.receiverId);
        return new ResponseEntity<>(messageService.sendMessage(
                token,
                userId,
                chatRoomId,
                messageRequest.text), HttpStatus.OK);
    }

    @RequestMapping(value = "/send_private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendPrivateMessage(@RequestBody MessageRequest messageRequest)
            throws UserNotFoundException, ChatRoomNotFoundException, AuthenticationException {
        Token token = messageRequest.tokenRequest.token;
        UserId userId = messageRequest.tokenRequest.userId;
        UserId receiverId = new UserId(messageRequest.receiverId);
        return new ResponseEntity<>(messageService.sendPrivateMessage(
                token,
                userId,
                receiverId,
                messageRequest.text), HttpStatus.OK);
    }
}

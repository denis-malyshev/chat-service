package com.teamdev.web.controller;

import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.MessageDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chatservice.wrappers.MessageRequest;
import com.teamdev.chatservice.wrappers.ReadMessagesRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@RequestMapping("/message")
@Controller
public class MessageServiceController {

    private static final Logger LOG = Logger.getLogger(MessageServiceController.class);

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageRequest messageRequest)
            throws UserNotFoundException, ChatRoomNotFoundException, AuthenticationException {
        try {
            return new ResponseEntity<>(messageService.sendMessage(
                    messageRequest.token,
                    messageRequest.userId,
                    new ChatRoomId(messageRequest.receiverId),
                    messageRequest.text), HttpStatus.OK);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/send_private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageDTO> sendPrivateMessage(@RequestBody MessageRequest messageRequest)
            throws AuthenticationException, UserNotFoundException {
        try {
            return new ResponseEntity<>(messageService.sendPrivateMessage(
                    messageRequest.token,
                    messageRequest.userId,
                    new UserId(messageRequest.receiverId),
                    messageRequest.text), HttpStatus.OK);
        } catch (AuthenticationException | UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find_all_after", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<MessageDTO>> findAllAfterDate(@RequestBody ReadMessagesRequest readMessagesRequest) {
        try {
            return new ResponseEntity<>(messageService.findAllAfterDate(
                    readMessagesRequest.token,
                    readMessagesRequest.userId,
                    readMessagesRequest.dateTime), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }
}
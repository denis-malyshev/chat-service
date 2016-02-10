package com.teamdev.web.controller;

import com.teamdev.chat.service.MessageService;
import com.teamdev.chatservice.wrappers.dto.ChatRoomId;
import com.teamdev.chatservice.wrappers.dto.MessageDTO;
import com.teamdev.chatservice.wrappers.dto.UserId;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin
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
                    readMessagesRequest.date), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find_all_received_private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<MessageDTO>> findAllReceivedPrivateAfterDate(@RequestBody ReadMessagesRequest readMessagesRequest) {
        try {
            return new ResponseEntity<>(messageService.findPrivateMessagesByReceiverIdAfterDate(
                    readMessagesRequest.token,
                    readMessagesRequest.userId,
                    readMessagesRequest.date), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find_all_sent_private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<MessageDTO>> findAllSentPrivateAfterDate(@RequestBody ReadMessagesRequest readMessagesRequest) {
        try {
            return new ResponseEntity<>(messageService.findPrivateMessagesBySenderIdAfterDate(
                    readMessagesRequest.token,
                    readMessagesRequest.userId,
                    readMessagesRequest.date), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/find_all_by_chat_after", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ArrayList<MessageDTO>> findAllByChatRoomIdAfterDate(@RequestBody ReadMessagesRequest readMessagesRequest) {
        try {
            return new ResponseEntity<>(messageService.findMessagesByChatRoomIdAfterDate(
                    readMessagesRequest.token,
                    readMessagesRequest.userId,
                    readMessagesRequest.chatRoomId,
                    readMessagesRequest.date), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }
}
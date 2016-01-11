package com.teamdev.web.controller;

import com.teamdev.chat.service.impl.ChatRoomServiceImpl;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@RequestMapping("/chat")
@Controller
public final class ChatServiceController {

    @Autowired
    private ChatRoomServiceImpl chatRoomService;

    @RequestMapping(value = "/chats/all", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<ChatRoomDTO> readAllChats_get() {
        return chatRoomService.findAll();
    }

    @RequestMapping(value = "/chats", params = {"token", "userId", "chatRoomId"})
    @ResponseBody
    public String deleteUserFromChat(@RequestParam String token, @RequestParam long userId, @RequestParam long chatRoomId)
            throws ChatRoomNotFoundException, UserNotFoundException, AuthenticationException {
        chatRoomService.leaveChatRoom(new Token(token), new UserId(userId), new ChatRoomId(chatRoomId));
        return token + userId;
    }

    @RequestMapping(value = "/create", params = {"token", "userId"}, method = RequestMethod.POST)
    @ResponseBody
    public ChatRoomDTO createChat(@RequestParam String token, @RequestParam long userId) throws ChatRoomAlreadyExistsException {
        return chatRoomService.create(new Token(token), new UserId(userId), "room");
    }


}

package com.teamdev.chat.service;

import com.teamdev.chatservice.wrappers.dto.ChatRoomDTO;
import com.teamdev.chatservice.wrappers.dto.ChatRoomId;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

public interface ChatRoomService {

    ChatRoomDTO create(Token token, UserId userId, String chatRoomName) throws ChatRoomAlreadyExistsException;

    void joinToChatRoom(Token token, UserId userId, ChatRoomId chatRoomId)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException;

    void leaveChatRoom(Token token, UserId userId, ChatRoomId chatRoomId)
            throws AuthenticationException, ChatRoomNotFoundException, UserNotFoundException;

    Collection<ChatRoomDTO> findByUserId(Token token, UserId userId);

    ArrayList<ChatRoomDTO> findAll(Token token, UserId userId);

}

package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;

import java.util.Collection;

public interface ChatRoomService {

    ChatRoomDTO create(String chatRoomName) throws ChatRoomAlreadyExistsException;

    void joinToChatRoom(Token token, UserId userId, ChatRoomId chatRoomId)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException;

    void leaveChatRoom(Token token, UserId userId, ChatRoomId chatRoomId)
            throws AuthenticationException, ChatRoomNotFoundException;

    Collection<ChatRoomDTO> findAll();

}

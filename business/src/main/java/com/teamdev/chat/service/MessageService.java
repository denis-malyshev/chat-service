package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.MessageDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface MessageService {

    MessageDTO sendMessage(Token token, UserId userId, ChatRoomId chatRoomId, String text)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException;

    MessageDTO sendPrivateMessage(Token token, UserId senderId, UserId receiverId, String text)
            throws AuthenticationException, UserNotFoundException;

    ArrayList<MessageDTO> findAllAfterDate(Token token, UserId userId, LocalDateTime dateTime);
}

package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chatservice.wrappers.dto.ChatRoomId;
import com.teamdev.chatservice.wrappers.dto.MessageDTO;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;

import java.util.ArrayList;
import java.util.Date;

public interface MessageService {

    MessageDTO sendMessage(Token token, UserId userId, ChatRoomId chatRoomId, String text)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException;

    MessageDTO sendPrivateMessage(Token token, UserId senderId, UserId receiverId, String text)
            throws AuthenticationException, UserNotFoundException;

    ArrayList<MessageDTO> findAllAfterDate(Token token, UserId userId, Date date);

    ArrayList<MessageDTO> findMessagesByChatRoomIdAfterDate(Token token, UserId userId, ChatRoomId chatRoomId, Date date);

    ArrayList<MessageDTO> findPrivateMessagesByReceiverIdAfterDate(Token token, UserId userId, Date date);

    ArrayList<MessageDTO> findPrivateMessagesBySenderIdAfterDate(Token token, UserId userId, Date date);
}

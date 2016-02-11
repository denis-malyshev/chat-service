package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chatservice.wrappers.dto.*;

import java.util.Collection;

public interface UserService {

    UserDTO register(UserDTO userDTO)
            throws AuthenticationException, RegistrationException;

    UserDTO findById(Token token, UserId searcherId, UserId userId) throws UserNotFoundException;

    Collection<UserDTO> findUsersByChatRoomId(Token token, UserId userId, ChatRoomId chatRoomId);

    String delete(Token token, UserId userId) throws UserNotFoundException;

    Collection<ChatRoomDTO> findAvailableChats(Token token, UserId userId);
}

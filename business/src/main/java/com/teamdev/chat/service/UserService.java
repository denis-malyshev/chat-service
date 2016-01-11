package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;

import java.util.ArrayList;

public interface UserService {

    UserDTO register(UserDTO userDTO)
            throws AuthenticationException, RegistrationException;

    UserDTO findById(Token token, UserId searcherId, UserId userId) throws UserNotFoundException;

    ArrayList<ChatRoomDTO> findAvailableChats(Token token, UserId userId);
}

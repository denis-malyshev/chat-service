package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;

import java.util.ArrayList;

public interface UserService {

    UserDTO register(UserName name, UserEmail email, UserPassword password)
            throws AuthenticationException, RegistrationException;

    UserDTO findById(UserId userId) throws UserNotFoundException;

    ArrayList<ChatRoomDTO> findAvailableChats(UserId userId);
}

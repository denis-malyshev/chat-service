package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;

import java.util.ArrayList;

public interface UserService {

    UserDTO register(UserName name, UserEmail email, UserPassword password)
            throws AuthenticationException, RegistrationException;

    UserDTO findById(UserId userId);

    ArrayList<ChatRoomDTO> findAvailableChats(UserId userId);
}

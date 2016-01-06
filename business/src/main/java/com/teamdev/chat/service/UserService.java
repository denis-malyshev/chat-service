package com.teamdev.chat.service;

import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.dto.UserName;
import com.teamdev.chat.service.impl.dto.UserPassword;

import java.util.Set;

public interface UserService {

    UserDTO register(UserName name, UserEmail email, UserPassword password) throws AuthenticationException;

    UserDTO findById(UserId userId);

    Set<ChatRoomDTO> findAvailableChats(UserId userId);
}

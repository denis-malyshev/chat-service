package com.teamdev.chat.service.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.teamdev.chat.service.impl.dto.UserEmail;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.dto.UserName;
import com.teamdev.chat.service.impl.dto.UserPassword;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Autowired
    private UserRepository userRepository;

    private HashFunction hashFunction = Hashing.md5();

    public UserServiceImpl() {
    }

    public UserDTO register(UserName name, UserEmail email, UserPassword password)
            throws AuthenticationException, RegistrationException {

        emailValidation(email);

        if (userRepository.userCount() > 0 && userRepository.findByMail(email.email) != null) {
            throw new AuthenticationException("User with the same mail already exists.");
        }

        String passwordHash = hashFunction.newHasher().putString(password.password, Charset.defaultCharset()).hash().toString();

        User user = new User(name.name, email.email, passwordHash);
        userRepository.update(user);
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail());
    }

    @Override
    public UserDTO findById(UserId userId) {

        User user = userRepository.findById(userId.id);

        if (user == null) {
            return null;
        }

        return new UserDTO(userId.id, user.getFirstName(), user.getEmail());
    }

    @Override
    public Set<ChatRoomDTO> findAvailableChats(UserId userId) {
        Set<ChatRoom> chatRooms = userRepository.findById(userId.id).getChatRooms();
        Set<ChatRoomDTO> chatRoomDTOs = new HashSet<>();
        for (ChatRoom chatRoom : chatRooms) {
            chatRoomDTOs.add(new ChatRoomDTO(chatRoom.getId(), chatRoom.getName(), chatRoom.getUsers().size(), chatRoom.getMessages().size()));
        }
        return chatRoomDTOs;
    }

    private void emailValidation(UserEmail email) throws RegistrationException {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email.email);

        if (!matcher.matches()) {
            throw new RegistrationException("Enter a correct email.");
        }
    }
}

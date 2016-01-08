package com.teamdev.chat.service.impl;

import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.teamdev.utils.JsonHelper.passwordHash;
import static java.lang.String.format;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl() {
    }

    public UserDTO register(UserName name, UserEmail email, UserPassword password)
            throws AuthenticationException, RegistrationException {

        LOG.info(format("Registration user %s.", email.email));

        emailValidation(email);

        if (userRepository.userCount() > 0 && userRepository.findByMail(email.email) != null) {
            throw new AuthenticationException("User with the same mail already exists.");
        }

        String passwordHash = passwordHash(password.password);

        User user = new User(name.name, email.email, passwordHash);
        userRepository.update(user);
        LOG.info(format("User %s successfully registered.", user.getFirstName()));
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail());
    }

    @Override
    public UserDTO findById(UserId userId) throws UserNotFoundException {
        LOG.info(format("Trying to find user with id[%d].", userId.id));
        User user = userRepository.findById(userId.id);

        if (user == null) {
            throw new UserNotFoundException(format("User with id[%d] not exists.", userId.id));
        }

        LOG.info(format("User %s was successfully found.", user.getEmail()));
        return new UserDTO(userId.id, user.getFirstName(), user.getEmail());
    }

    @Override
    public ArrayList<ChatRoomDTO> findAvailableChats(UserId userId) {
        Set<ChatRoom> chatRooms = userRepository.findById(userId.id).getChatRooms();
        return chatRooms.stream().
                map(chatRoom -> new ChatRoomDTO(
                        chatRoom.getId(),
                        chatRoom.getName(),
                        chatRoom.getUsers().size(),
                        chatRoom.getMessages().size())).
                collect(Collectors.toCollection(ArrayList<ChatRoomDTO>::new));
    }

    private void emailValidation(UserEmail email) throws RegistrationException {
        LOG.info("Checking an email address is correct or not.");

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email.email);

        if (!matcher.matches()) {
            throw new RegistrationException("Enter a correct email.");
        }

        LOG.info("Email address is correct.");
    }
}

package com.teamdev.chat.service.impl.jpa;

import com.teamdev.chat.persistence.jpa.dom.ChatRoom;
import com.teamdev.chat.persistence.jpa.dom.User;
import com.teamdev.chat.persistence.jpa.repository.UserRepository;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.RegistrationException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.teamdev.utils.Hasher.createHash;
import static java.lang.String.format;


public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired(required = false)
    private UserRepository userRepository;

    public UserServiceImpl() {
    }

    public UserDTO register(UserDTO userDTO)
            throws AuthenticationException, RegistrationException {

        LOG.info(format("Registration user %s.", userDTO.email));

        emailValidation(userDTO.email);

//        if (userRepository.count() > 0 && userRepository.findByEmail(userDTO.email) != null) {
//            throw new AuthenticationException("User with the same mail already exists.");
//        }

        String passwordHash = createHash(userDTO.password);

        User user = new User(userDTO.firstName, userDTO.email, passwordHash);
        userRepository.save(user);
        LOG.info(format("User %s successfully registered.", user.getFirstName()));
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail());
    }

    @Override
    public UserDTO findById(Token token, UserId searcherId, UserId userId) throws UserNotFoundException {
        LOG.info(format("Trying to find user with id[%d].", userId.id));
        User user = userRepository.findOne(userId.id);

        if (user == null) {
            throw new UserNotFoundException(format("User with id[%d] not exists.", userId.id));
        }

        LOG.info(format("User %s was successfully found.", user.getEmail()));
        return new UserDTO(userId.id, user.getFirstName(), user.getEmail());
    }

    @Override
    public ArrayList<ChatRoomDTO> findAvailableChats(Token token, UserId userId) {
        Set<ChatRoom> chatRooms = userRepository.findOne(userId.id).getChatRooms();
        return chatRooms.stream().
                map(chatRoom -> new ChatRoomDTO(
                        chatRoom.getId(),
                        chatRoom.getName(),
                        chatRoom.getUsers().size(),
                        chatRoom.getMessages().size())).
                collect(Collectors.toCollection(ArrayList<ChatRoomDTO>::new));
    }

    private void emailValidation(String email) throws RegistrationException {
        LOG.info("Checking an email address is correct or not.");

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new RegistrationException("Enter a correct email.");
        }

        LOG.info("Email address is correct.");
    }
}

package com.teamdev.chat.service.impl;

import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static java.lang.String.format;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private static final Logger LOG = Logger.getLogger(ChatRoomServiceImpl.class);
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;

    public ChatRoomServiceImpl() {
    }

    public ChatRoomDTO create(Token token, UserId userId, String chatRoomName) throws ChatRoomAlreadyExistsException {
        LOG.info(format("Creating chat-room \"%s.\"", chatRoomName));

        if (chatRoomRepository.count() > 0 && chatRoomRepository.findByName(chatRoomName) != null) {
            throw new ChatRoomAlreadyExistsException(format("ChatRoom %s already exists.", chatRoomName));
        }

        ChatRoom chatRoom = new ChatRoom(chatRoomName);
        chatRoomRepository.save(chatRoom);
        LOG.info(format("Chat-room \"%s\" created successfully.", chatRoom.getName()));
        return new ChatRoomDTO(chatRoom.getId(), chatRoom.getName(), 0, 0);
    }

    public void joinToChatRoom(Token token,
                               UserId userId,
                               ChatRoomId chatRoomId)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException {

        LOG.info(format("Join user with id[%d] into chat-room with id[%d].", userId.id, chatRoomId.id));

        ChatRoom chatRoom = getChatRoom(chatRoomId);
        User user = getUser(userId);

        user.addChatRoom(chatRoom);
        chatRoom.addUser(user);

        userRepository.save(user);
        chatRoomRepository.save(chatRoom);

        LOG.info(format("Joined user with id[%d] into chat-room with id[%d] was successfully.", userId.id, chatRoomId.id));
    }

    public void leaveChatRoom(Token token,
                              UserId userId,
                              ChatRoomId chatRoomId)
            throws AuthenticationException, ChatRoomNotFoundException, UserNotFoundException {

        LOG.info(format("Deleting user with id[%d] from chat-room with id[%d].", userId.id, chatRoomId.id));

        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        boolean removeUser = user.getChatRooms().remove(chatRoom);
        boolean removeChat = chatRoom.getUsers().remove(user);

        if (removeUser && removeChat) {
            LOG.info(String.format("User[%d] was successfully deleted from chat-room[%d].", userId.id, chatRoomId.id));
        }
    }

    @Override
    public ArrayList<ChatRoomDTO> findAll(Token token, UserId userId) {
        Iterable<ChatRoom> chatRooms = chatRoomRepository.findAll();
        ArrayList<ChatRoomDTO> chatRoomDTOs = new ArrayList<>();
        for (ChatRoom chat : chatRooms) {
            chatRoomDTOs.add(new ChatRoomDTO(
                    chat.getId(),
                    chat.getName(),
                    chat.getUsers().size(),
                    chat.getMessages().size()));
        }
        return chatRoomDTOs;
    }

    private ChatRoom getChatRoom(ChatRoomId chatRoomId) throws ChatRoomNotFoundException {
        ChatRoom chatRoom = chatRoomRepository.findOne(chatRoomId.id);

        if (chatRoom == null) {
            throw new ChatRoomNotFoundException(format("ChatRoom with this id [%d] not exists.", chatRoomId.id));
        }
        return chatRoom;
    }

    private User getUser(UserId userId) throws UserNotFoundException {
        User user = userRepository.findOne(userId.id);

        if (user == null) {
            throw new UserNotFoundException(format("User with this id [%d] not exists.", userId.id));
        }
        return user;
    }
}

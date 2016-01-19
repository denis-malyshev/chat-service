package com.teamdev.chat.service.impl;

import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.MessageRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.Message;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.MessageDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = Logger.getLogger(MessageServiceImpl.class);
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public MessageServiceImpl() {
    }

    @Override
    public MessageDTO sendMessage(Token token, UserId userId, ChatRoomId chatRoomId, String text)
            throws AuthenticationException, UserNotFoundException, ChatRoomNotFoundException {

        LOG.info(String.format("User with id[%d] sending message to chatRoom with id[%d].", userId.id, chatRoomId.id));

        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        Message message = messageRepository.save(new Message(text, user, chatRoom));

        user.getMessages().add(message);
        chatRoom.getMessages().add(message);

        userRepository.save(user);
        chatRoomRepository.save(chatRoom);

        LOG.info("Message sent successfully.");
        return new MessageDTO(message.getId(), message.getText(), message.getTime());
    }

    @Override
    public MessageDTO sendPrivateMessage(Token token, UserId senderId, UserId receiverId, String text)
            throws AuthenticationException, UserNotFoundException {

        LOG.info(String.format("User with id[%d] sending message to user with id[%d].", senderId.id, receiverId.id));

        User sender = getUser(senderId);
        User receiver = getUser(receiverId);

        Message message = messageRepository.save(new Message(text, sender, receiver));

        sender.getMessages().add(message);
        receiver.getMessages().add(message);

        userRepository.save(sender);
        userRepository.save(receiver);

        LOG.info("Message sent successfully.");
        return new MessageDTO(message.getId(), message.getText(), message.getTime());
    }

    @Override
    public ArrayList<MessageDTO> findAllAfterDate(Token token, UserId userId, LocalDateTime dateTime) {
        List<Message> messages = messageRepository.findByTimeAfter(dateTime);
        return messages.stream().map(message ->
                new MessageDTO(
                        message.getId(),
                        message.getText(),
                        message.getTime())).
                collect(Collectors.toCollection(ArrayList<MessageDTO>::new));
    }

    private ChatRoom getChatRoom(ChatRoomId chatRoomId) throws ChatRoomNotFoundException {
        ChatRoom chatRoom = chatRoomRepository.findOne(chatRoomId.id);

        if (chatRoom == null) {
            throw new ChatRoomNotFoundException(String.format("ChatRoom with this id [%d] not exists.", chatRoomId.id));
        }
        return chatRoom;
    }

    private User getUser(UserId userId) throws UserNotFoundException {
        User user = userRepository.findOne(userId.id);

        if (user == null) {
            throw new UserNotFoundException(String.format("User with this id [%d] not exists.", userId.id));
        }
        return user;
    }
}

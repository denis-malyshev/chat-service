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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

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

        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        Message message = new Message(text, user, chatRoom);
        messageRepository.update(message);

        user.getMessages().add(message);
        chatRoom.getMessages().add(message);

        return new MessageDTO(message.getId(), message.getText(), message.getTime());
    }

    @Override
    public MessageDTO sendPrivateMessage(Token token, UserId senderId, UserId receiverId, String text)
            throws AuthenticationException, UserNotFoundException {

        User sender = getUser(senderId);
        User receiver = getUser(receiverId);

        Message message = new Message(text, sender, receiver);
        messageRepository.update(message);

        sender.getMessages().add(message);
        receiver.getMessages().add(message);

        return new MessageDTO(message.getId(), message.getText(), message.getTime());
    }

    private ChatRoom getChatRoom(ChatRoomId chatRoomId) throws ChatRoomNotFoundException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId.id);

        if (chatRoom == null) {
            throw new ChatRoomNotFoundException(String.format("ChatRoom with this id [%d] not exists.", chatRoomId.id));
        }
        return chatRoom;
    }

    private User getUser(UserId userId) throws UserNotFoundException {
        User user = userRepository.findById(userId.id);

        if (user == null) {
            throw new UserNotFoundException(String.format("User with this id [%d] not exists.", userId.id));
        }
        return user;
    }
}

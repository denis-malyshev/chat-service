package com.teamdev.chat.service.impl;

import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.MessageRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.Message;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chatservice.wrappers.dto.ChatRoomId;
import com.teamdev.chatservice.wrappers.dto.MessageDTO;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

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

        LOG.trace(format("User with id[%d] sending message to chatRoom with id[%d].", userId.id, chatRoomId.id));

        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        Message message = messageRepository.save(new Message(text, user, chatRoom));

        user.getSentMessages().add(message);
        chatRoom.getMessages().add(message);

        userRepository.save(user);
        chatRoomRepository.save(chatRoom);

        LOG.trace("Message sent successfully.");
        return new MessageDTO(message.getId(), message.getSender().getFirstName(), "chat-room" + chatRoomId.id, message.getText(), message.getCreatingTime());
    }

    @Override
    public MessageDTO sendPrivateMessage(Token token, UserId senderId, UserId receiverId, String text)
            throws AuthenticationException, UserNotFoundException {

        LOG.trace(format("User with id[%d] sending message to user with id[%d].", senderId.id, receiverId.id));

        User sender = getUser(senderId);
        User receiver = getUser(receiverId);

        Message message = messageRepository.save(new Message(text, sender, receiver));

        sender.getSentMessages().add(message);
        receiver.getReceivedMessages().add(message);

        userRepository.save(sender);
        userRepository.save(receiver);

        LOG.trace("Message sent successfully.");
        return new MessageDTO(message.getId(), message.getSender().getFirstName(), message.getReceiver().getFirstName(), message.getText(), message.getCreatingTime());
    }

    @Override
    public ArrayList<MessageDTO> findAllAfterDate(Token token, UserId userId, Date date) {
        List<Message> messages = messageRepository.findByCreatingTimeAfter(date);
        return messages.stream().map(message ->
                new MessageDTO(
                        message.getId(),
                        message.getSender().getFirstName(),
                        "chat-room",
                        message.getText(),
                        message.getCreatingTime())).
                collect(Collectors.toCollection(ArrayList<MessageDTO>::new));
    }

    @Override
    public ArrayList<MessageDTO> findPrivateMessagesByReceiverIdAfterDate(
            Token token, UserId userId, Date date) {
        LOG.trace(format("Try to read private-messages by receiverId[%d] after date", userId.id));

        List<Message> messages = messageRepository.findMessageByReceiverIdAfterDate(userId.id, date);
        ArrayList<MessageDTO> result = messages.stream().map(message ->
                new MessageDTO(
                        message.getId(),
                        message.getSender().getFirstName(),
                        message.getReceiver().getFirstName(),
                        message.getText(),
                        message.getCreatingTime())).
                collect(Collectors.toCollection(ArrayList<MessageDTO>::new));

        LOG.trace("successful");
        return result;
    }

    @Override
    public ArrayList<MessageDTO> findPrivateMessagesBySenderIdAfterDate(
            Token token, UserId userId, Date date) {

        LOG.trace(format("Try to read private-messages by senderId[%d] after date", userId.id));

        List<Message> messages = messageRepository.findMessageBySenderIdAfterDate(userId.id, date);
        ArrayList<MessageDTO> result = messages.stream().map(message ->
                new MessageDTO(
                        message.getId(),
                        message.getSender().getFirstName(),
                        message.getReceiver().getFirstName(),
                        message.getText(),
                        message.getCreatingTime())).
                collect(Collectors.toCollection(ArrayList<MessageDTO>::new));

        LOG.trace("successful");
        return result;
    }

    @Override
    public ArrayList<MessageDTO> findMessagesByChatRoomIdAfterDate(
            Token token, UserId userId, ChatRoomId chatRoomId, Date date) {
        LOG.trace(format("Try to read private-messages by chatRoomId[%d] after date", chatRoomId.id));

        List<Message> messages = messageRepository.findMessagesByChatRoomIdAfterDate(chatRoomId.id, date);
        ArrayList<MessageDTO> collect = messages.stream().map(message ->
                new MessageDTO(
                        message.getId(),
                        message.getSender().getFirstName(),
                        "chat-room" + chatRoomId.id,
                        message.getText(),
                        message.getCreatingTime())).
                collect(Collectors.toCollection(ArrayList<MessageDTO>::new));

        LOG.trace("successful");
        return collect;
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

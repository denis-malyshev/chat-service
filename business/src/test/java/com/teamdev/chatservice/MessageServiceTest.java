package com.teamdev.chatservice;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.MessageRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.MessageDTO;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class MessageServiceTest {

    private MessageService messageService;

    private UserId senderId;
    private UserId recipientId;

    private ChatRoomId chatRoomId;

    private Token token;

    @Before
    public void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        messageService = context.getBean(MessageService.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        ChatRoomRepository chatRoomRepository = context.getBean(ChatRoomRepository.class);

        ChatRoom chatRoom = new ChatRoom("test-chat");
        chatRoomRepository.save(chatRoom);
        chatRoomId = new ChatRoomId(chatRoom.getId());

        User user1 = new User("Vasya", "vasya.message.service@gmail.com", "pwd1");
        User user2 = new User("Masha", "masha.message.service@gmail.com", "pwd");

        userRepository.save(user1);
        userRepository.save(user2);

        senderId = new UserId(user1.getId());
        recipientId = new UserId(user2.getId());

        AuthenticationToken authenticationToken = new AuthenticationToken(user1.getId());
        AuthenticationTokenRepository tokenRepository = context.getBean(AuthenticationTokenRepository.class);
        tokenRepository.save(authenticationToken);
        token = new Token(authenticationToken.getKey());
    }

    @Test
    public void testSendMessage_MessageRepositoryCanNotBeEmpty() {
        try {
            MessageDTO messageDTO = messageService.sendMessage(token, senderId, chatRoomId, "Hello, Masha!");
            assertNotNull(messageDTO);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendPrivateMessage_MessageRepositoryCanNotBeEmpty() {
        try {
            MessageDTO messageDTO = messageService.sendPrivateMessage(token, senderId, recipientId, "Hello");
            assertNotNull(messageDTO);
        } catch (AuthenticationException | UserNotFoundException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendMessageToNotExistingChat() {

        try {
            messageService.sendMessage(token, senderId, new ChatRoomId(999L), "Hello, Masha!");
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom with this id [999] not exists.", result);
        }
    }

    @Test
    public void testSendMessageToNotExistingUser() {

        try {
            messageService.sendPrivateMessage(token, senderId, new UserId(999L), "Hello, Masha!");
        } catch (AuthenticationException | UserNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "User with this id [999] not exists.", result);
        }
    }
}

package com.teamdev.chatservice;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.application.ApplicationConfig;
import com.teamdev.chat.service.impl.dto.ChatRoomId;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;
import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.MessageRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MessageServiceTest {

    private MessageService messageService;
    private MessageRepository messageRepository;

    private UserId senderId;
    private UserId recipientId;

    private ChatRoomId chatRoomId;

    private Token token;

    @Before
    public void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        messageService = context.getBean(MessageService.class);
        messageRepository = context.getBean(MessageRepository.class);
        UserService userService = context.getBean(UserService.class);
        ChatRoomRepository chatRoomRepository = context.getBean(ChatRoomRepository.class);

        ChatRoom chatRoom = new ChatRoom("test-chat");
        chatRoomRepository.update(chatRoom);
        chatRoomId = new ChatRoomId(chatRoom.getId());

        User user1 = new User("Vasya", "vasya@gmail.com", "pwd1");
        User user2 = new User("Masha", "masha@gmail.com", "pwd");

        UserDTO userDTO1 = TestUtils.registerUser(userService,user1);

        UserDTO userDTO2 = TestUtils.registerUser(userService, user2);

        senderId = new UserId(userDTO1.id);
        recipientId = new UserId(userDTO2.id);

        AuthenticationService tokenService = context.getBean(AuthenticationService.class);
        token = TestUtils.loginUser(userDTO1, tokenService);
    }

    @Test
    public void testSendMessage_MessageRepositoryCanNotBeEmpty() throws Exception {

        messageService.sendMessage(token, senderId, chatRoomId, "Hello, Masha!");
        boolean result = messageRepository.findAll().isEmpty();
        assertFalse(result);
    }

    @Test
    public void testSendPrivateMessage_MessageRepositoryCanNotBeEmpty() throws Exception {

        messageService.sendPrivateMessage(token, senderId, recipientId, "Hello, Masha!");
        boolean result = messageRepository.findAll().isEmpty();
        assertFalse(result);
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

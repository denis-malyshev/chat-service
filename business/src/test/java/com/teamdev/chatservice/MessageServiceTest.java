package com.teamdev.chatservice;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class MessageServiceTest extends AbstractSpringContext {

    private static final Random RANDOM = new Random();

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private AuthenticationService authenticationService;

    private static UserId testUserId;
    private static ChatRoomId testChatRoomId;
    private static Token testToken;

    @Before
    public void setUp() throws RegistrationException, ChatRoomAlreadyExistsException {
        final int identifier = RANDOM.nextInt();
        String testEmail = String.format("test.user%d@gmail.com", identifier);
        UserDTO testUser = userService.register(new UserDTO("Vasya", testEmail, "pwd"));
        testUserId = new UserId(testUser.id);
        testToken = authenticationService.login(new LoginInfo(testEmail, "pwd"));
        String testChatRoomName = "chat-room-" + identifier;
        ChatRoomDTO chatRoomDTO = chatRoomService.create(testToken, testUserId, testChatRoomName);
        testChatRoomId = new ChatRoomId(chatRoomDTO.id);
    }

    @Test
    public void testSendMessage() {
        try {
            MessageDTO messageDTO = messageService.sendMessage(testToken, testUserId, testChatRoomId, "Hello, Masha!");
            assertNotNull(messageDTO);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendPrivateMessage() throws RegistrationException {
        UserDTO register = userService.register(new UserDTO("Vasya", "recipien@gmail.com", "pwd"));
        try {
            MessageDTO messageDTO = messageService.sendPrivateMessage(testToken, testUserId, new UserId(register.id), "Hello");
            assertNotNull(messageDTO);
        } catch (AuthenticationException | UserNotFoundException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendMessageToNotExistingChat() {

        try {
            messageService.sendMessage(testToken, testUserId, new ChatRoomId(999L), "Hello, Masha!");
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom with this id [999] not exists.", result);
        }
    }

    @Test
    public void testSendMessageToNotExistingUser() {

        try {
            messageService.sendPrivateMessage(testToken, testUserId, new UserId(999L), "Hello, Masha!");
        } catch (AuthenticationException | UserNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "User with this id [999] not exists.", result);
        }
    }

    @Test
    public void testFindAllAfterDate() throws Exception {
        ArrayList<MessageDTO> result = messageService.findAllAfterDate(testToken, testUserId, new Date());
        assertNotNull("Result can't be null.", result);
    }
}

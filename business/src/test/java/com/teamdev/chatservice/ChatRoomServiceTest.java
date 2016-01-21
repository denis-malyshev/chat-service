package com.teamdev.chatservice;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

import static org.junit.Assert.*;

public class ChatRoomServiceTest extends SpringContextRunner {

    private static final Random RANDOM = new Random();

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;
    private UserId testUserId;
    private Token testToken;
    private ChatRoomId testChatRoomId;

    @Before
    public void setUp() throws RegistrationException, ChatRoomAlreadyExistsException {
        final int identifier = RANDOM.nextInt();
        String testEmail = String.format("vasya.chat.service%d@gmail.com", identifier);
        UserDTO testUser = userService.register(new UserDTO("Vasya", testEmail, "pwd"));
        testUserId = new UserId(testUser.id);
        testToken = authenticationService.login(new LoginInfo(testEmail, "pwd"));
        String testChatRoomName = "chat-room-" + identifier;
        ChatRoomDTO chatRoomDTO = chatRoomService.create(testToken, testUserId, testChatRoomName);
        testChatRoomId = new ChatRoomId(chatRoomDTO.id);
    }

    @Test
    public void test_create_chat() {
        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.create(testToken, testUserId, "chat");
            assertNotNull("ChatRoomDTO must exists.", chatRoomDTO);
        } catch (ChatRoomAlreadyExistsException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test_create_chat_with_existing_name() {
        try {
            chatRoomService.create(testToken, testUserId, "chat-1");
            chatRoomService.create(testToken, testUserId, "chat-1");

            fail();
        } catch (ChatRoomAlreadyExistsException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom chat-1 already exists.", result);
        }
    }

    @Test
    public void test_join_user_yo_empty_chat() {
        try {
            chatRoomService.joinToChatRoom(testToken, testUserId, testChatRoomId);
        } catch (UserNotFoundException | ChatRoomNotFoundException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test_join_user_to_not_existing_chat() {
        try {
            chatRoomService.joinToChatRoom(testToken, testUserId, new ChatRoomId(34876L));
            fail();
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom with this id [34876] not exists.", result);
        }
    }

    @Test
    public void test_delete_user_from_chat() {
        try {
            chatRoomService.leaveChatRoom(testToken, testUserId, testChatRoomId);
        } catch (AuthenticationException | ChatRoomNotFoundException | UserNotFoundException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}

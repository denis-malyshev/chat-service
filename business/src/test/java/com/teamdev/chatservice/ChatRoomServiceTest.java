package com.teamdev.chatservice;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
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
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class ChatRoomServiceTest {

    private ChatRoomService chatRoomService;
    private ChatRoomRepository chatRoomRepository;
    private User user;
    private UserId userId;
    private Token token;
    private ChatRoomId chatRoomId;

    @Before
    public void setUp() {

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        chatRoomService = context.getBean(ChatRoomService.class);
        chatRoomRepository = context.getBean(ChatRoomRepository.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        AuthenticationTokenRepository tokenRepository = context.getBean(AuthenticationTokenRepository.class);

        user = new User("Vasya", "vasya@gmail.com", "pwd");
        userRepository.update(user);
        userId = new UserId(user.getId());

        AuthenticationToken authToken = new AuthenticationToken(userId.id);
        tokenRepository.update(authToken);
        token = new Token(authToken.getKey());

        ChatRoom chatRoom = new ChatRoom("chat-1");
        chatRoomRepository.update(chatRoom);
        chatRoomId = new ChatRoomId(chatRoom.getId());
    }

    @Test
    public void testCreateChat() {
        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.create(token, userId, "chat");
            assertNotNull("ChatRoomDTO must exists.", chatRoomDTO);
        } catch (ChatRoomAlreadyExistsException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testCreateChatWithExistingName() {
        try {
            chatRoomService.create(token, userId, "chat-1");
            fail();
        } catch (ChatRoomAlreadyExistsException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom chat-1 already exists.", result);
        }
    }

    @Test
    public void testJoinUserToEmptyChat() {
        try {
            chatRoomService.joinToChatRoom(token, userId, chatRoomId);
            int result = chatRoomRepository.findById(chatRoomId.id).getUsers().size();
            assertEquals("The count of users must be 1", 1, result);
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testJoinUserToNotExistingChat() {
        try {
            chatRoomService.joinToChatRoom(token, userId, new ChatRoomId(34876L));
            fail();
        } catch (AuthenticationException | UserNotFoundException | ChatRoomNotFoundException e) {
            String result = e.getMessage();
            assertEquals("Exception message must be correct.", "ChatRoom with this id [34876] not exists.", result);
        }
    }

    @Test
    public void testDeleteUserFromChat() {
        chatRoomRepository.findById(chatRoomId.id).getUsers().add(user);

        try {
            chatRoomService.leaveChatRoom(token, userId, chatRoomId);
            boolean result = chatRoomRepository.findById(chatRoomId.id).getUsers().isEmpty();
            assertTrue("The count of users in chatRoom must be 0", result);
        } catch (AuthenticationException | ChatRoomNotFoundException | UserNotFoundException e) {
            fail("Unexpected exception.");
        }
    }
}

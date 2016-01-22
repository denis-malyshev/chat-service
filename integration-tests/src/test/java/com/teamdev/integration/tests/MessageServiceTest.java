package com.teamdev.integration.tests;

import com.google.common.reflect.TypeToken;
import com.teamdev.chatservice.wrappers.ChatRoomRequest;
import com.teamdev.chatservice.wrappers.MessageRequest;
import com.teamdev.chatservice.wrappers.ReadMessagesRequest;
import com.teamdev.chatservice.wrappers.dto.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.teamdev.integration.tests.AuthenticationServiceTest.getTokenFromResponse;
import static com.teamdev.integration.tests.ChatRoomServiceTest.*;
import static com.teamdev.integration.tests.UserServiceTest.getUserFromResponse;
import static com.teamdev.integration.tests.UserServiceTest.register;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static java.lang.String.format;
import static org.junit.Assert.*;

public class MessageServiceTest {
    private static final Logger LOG = Logger.getLogger(MessageServiceTest.class);
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String MESSAGE_SERVICE_URL = HOME_URL + "/message";
    private static final String SEND_URL = MESSAGE_SERVICE_URL + "/send";
    private static final String SEND_PRIVATE_URL = MESSAGE_SERVICE_URL + "/send_private";
    private static final String FIND_ALL_AFTER = MESSAGE_SERVICE_URL + "/find_all_after";
    private static final Random RANDOM = new Random();
    public static final int FIVE_MINUTES = 1000 * 60 * 5;

    private static UserId testUserId;
    private static Token testToken;
    private static ChatRoomId testChatRoomId;
    private static CloseableHttpClient httpClient;

    @Before
    public  void setUp() {
        httpClient = HttpClients.createDefault();
        final int identifier = RANDOM.nextInt();
        String testUserEmail = format("messageservice%d@gmail.com", identifier);
        String testChatRoomName = format("testChatForMessageService%d", identifier);
        UserDTO userDTO = new UserDTO(
                "VasyaFromMessageService",
                testUserEmail,
                identifier + "");
        try {
            UserDTO testUserDTO = getUserFromResponse(register(userDTO));
            testToken = getTokenFromResponse(AuthenticationServiceTest.login(
                    new LoginInfo(userDTO.email, userDTO.password)));
            ChatRoomDTO testChatDTO = getChatFromResponse(create(
                    new ChatRoomRequest(testToken, new UserId(testUserDTO.id), testChatRoomName)));
            testUserId = new UserId(testUserDTO.id);
            testChatRoomId = new ChatRoomId(testChatDTO.id);
            joinUserToChat(testToken, testUserId, testChatRoomId);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void test_send_message_to_existing_chat() {
        try {
            String text = "Hello!";
            CloseableHttpResponse response = sendMessage(testToken, testUserId, testChatRoomId.id, text, false);
            String json = contentToString(response);
            MessageDTO messageDTO = fromJson(json, MessageDTO.class);
            assertEquals("Message texts must be equals.", text, messageDTO.text);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_send_message_to_not_existing_chat() {
        try {
            CloseableHttpResponse response = sendMessage(testToken, testUserId, 999, "Hello!", false);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 404, result);
            assertEquals("Error message must be correct.", "ChatRoom with this id [999] not exists.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_send_private_message() {
        final int identifier = RANDOM.nextInt();
        String testEmail = format("serega%d@gmail.com", identifier);
        try {
            UserDTO userDTO = new UserDTO("Serega", testEmail, "pwd");
            CloseableHttpResponse httpResponse = register(userDTO);
            UserDTO registeredUser = fromJson(contentToString(httpResponse), UserDTO.class);

            CloseableHttpResponse response = sendMessage(testToken, testUserId, registeredUser.id, "Hello!", true);
            String json = contentToString(response);
            MessageDTO messageDTO = fromJson(json, MessageDTO.class);
            assertEquals("Text messages must be equals.", "Hello!", messageDTO.text);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_send_private_message_to_not_existing_user() {
        try {
            CloseableHttpResponse response = sendMessage(testToken, testUserId, 999, "Hello!", true);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 404, result);
            assertEquals("Error message must be correct.", "User with this id [999] not exists.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_find_all_messages_after_date() {
        try {
            HttpPost httpPost = new HttpPost(FIND_ALL_AFTER);
            ReadMessagesRequest readMessagesRequest = new ReadMessagesRequest(testToken, testUserId, new Date(System.currentTimeMillis() - FIVE_MINUTES));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(readMessagesRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String json = contentToString(response);
            ArrayList<MessageDTO> result = fromJson(json, new TypeToken<ArrayList<MessageDTO>>() {
            }.getType());
            assertNotNull("Result can't be null.", result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    public static CloseableHttpResponse sendMessage(
            Token token,
            UserId userId,
            long receiverId,
            String text,
            boolean isPrivate) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(isPrivate ? SEND_PRIVATE_URL : SEND_URL);
        MessageRequest messageRequest = new MessageRequest(token, userId, receiverId, text);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(messageRequest)));

        return httpClient.execute(httpPost);
    }
}

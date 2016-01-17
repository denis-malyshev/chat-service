package com.teamdev.integration.tests;

import com.google.common.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chatservice.wrappers.ChatRoomRequest;
import com.teamdev.chatservice.wrappers.MessageRequest;
import com.teamdev.chatservice.wrappers.ReadMessagesRequest;
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
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.teamdev.integration.tests.AuthenticationServiceTest.getTokenFromResponse;
import static com.teamdev.integration.tests.ChatRoomServiceTest.*;
import static com.teamdev.integration.tests.UserServiceTest.getUserFromResponse;
import static com.teamdev.integration.tests.UserServiceTest.register;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static org.junit.Assert.*;

public class MessageServiceTest {
    private static final Logger LOG = Logger.getLogger(MessageServiceTest.class);
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String MESSAGE_SERVICE_URL = HOME_URL + "/message";
    private static final String SEND_URL = MESSAGE_SERVICE_URL + "/send";
    private static final String SEND_PRIVATE_URL = MESSAGE_SERVICE_URL + "/send_private";
    private static final String FIND_ALL_AFTER = MESSAGE_SERVICE_URL + "/find_all_after";

    private static UserId testUserId;
    private static Token testToken;
    private static ChatRoomId testChatRoomId;

    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void beforeClass() {
        try {
            UserDTO userDTO = new UserDTO(
                    "VasyaFromMessageService",
                    "messageservice@gmail.com",
                    "messaheservice");
            UserDTO testUserDTO = getUserFromResponse(register(userDTO));
            testToken = getTokenFromResponse(AuthenticationServiceTest.login(
                    new LoginInfo(userDTO.email, userDTO.password)));
            ChatRoomDTO testChatDTO = getChatFromResponse(create(
                    new ChatRoomRequest(testToken, new UserId(testUserDTO.id), "testChatForMessageService")));
            testUserId = new UserId(testUserDTO.id);
            testChatRoomId = new ChatRoomId(testChatDTO.id);
            joinUserToChat(testToken, testUserId, testChatRoomId);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testSendMessageToExistingChat() {
        try {
            HttpPost httpPost = new HttpPost(SEND_URL);
            MessageRequest messageRequest = new MessageRequest(testToken, testUserId, testChatRoomId.id, "Hello!");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(messageRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String json = contentToString(response);
            MessageDTO messageDTO = fromJson(json, MessageDTO.class);
            assertEquals("ChatRoom names must be equals.", messageRequest.text, messageDTO.text);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendMessageToNotExistingChat() {
        try {
            HttpPost httpPost = new HttpPost(SEND_URL);
            MessageRequest messageRequest = new MessageRequest(testToken, testUserId, 999, "Hello!");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(messageRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 404, result);
            assertEquals("Error message must be correct.", "ChatRoom with this id [999] not exists.", message);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendPrivateMessage() {
        try {
            UserDTO userDTO = new UserDTO("Serega", "serega@gmail.com", "pwd");
            CloseableHttpResponse httpResponse = register(userDTO);
            UserDTO registeredUser = fromJson(contentToString(httpResponse), UserDTO.class);

            HttpPost httpPost = new HttpPost(SEND_PRIVATE_URL);
            MessageRequest messageRequest = new MessageRequest(testToken, testUserId, registeredUser.id, "Hello!");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(messageRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String json = contentToString(response);
            MessageDTO messageDTO = fromJson(json, MessageDTO.class);
            assertEquals("Text messages must be equals.", messageRequest.text, messageDTO.text);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testSendPrivateMessageToNotExistingUser() {
        try {
            HttpPost httpPost = new HttpPost(SEND_PRIVATE_URL);
            MessageRequest messageRequest = new MessageRequest(testToken, testUserId, 999, "Hello!");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(messageRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 404, result);
            assertEquals("Error message must be correct.", "User with this id [999] not exists.", message);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fail("Unexpected exception.");
        }
    }

    @Test
    public void testFindAllMessagesAfterDate() {
        try {
            HttpPost httpPost = new HttpPost(FIND_ALL_AFTER);
            ReadMessagesRequest readMessagesRequest = new ReadMessagesRequest(testToken, testUserId, LocalDateTime.now().minusDays(1L));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(readMessagesRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String json = contentToString(response);
            ArrayList<MessageDTO> result = fromJson(json, new TypeToken<ArrayList<MessageDTO>>() {
            }.getType());
            assertNotNull("Result can't be null.", result);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fail("Unexpected exception.");
        }
    }
}

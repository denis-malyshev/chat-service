package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.integration.tests.wrappers.MessageRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.teamdev.integration.tests.AuthenticationServiceTest.login;
import static com.teamdev.integration.tests.UserServiceTest.register;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static org.junit.Assert.assertEquals;

public class MessageServiceTest {

    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String MESSAGE_SERVICE_URL = HOME_URL + "/message";
    private static final String SEND_URL = MESSAGE_SERVICE_URL + "/send";
    private static final String SEND_PRIVATE_URL = MESSAGE_SERVICE_URL + "/send_private";

    private static final LoginInfo TEST_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");
    private static final UserId TEST_USER_ID = new UserId(1);
    private static final ChatRoomId TEST_CHAT_ID = new ChatRoomId(1);

    private static CloseableHttpClient httpClient;
    private Token token;

    @Before
    public void setUp() throws IOException {
        httpClient = HttpClients.createDefault();
        token = login(TEST_LOGIN_INFO, httpClient);
    }

    @Test
    public void testSendMessageToExistingChat() throws IOException {
        HttpPost httpPost = new HttpPost(SEND_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, TEST_CHAT_ID.id, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = contentToString(response);
        MessageDTO messageDTO = fromJson(json, MessageDTO.class);
        assertEquals("ChatRoom names must be equals.", messageRequest.text, messageDTO.text);
    }

    @Test
    public void testSendMessageToNotExistingChat() throws IOException {
        HttpPost httpPost = new HttpPost(SEND_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, 999, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String message = contentToString(response);
        int result = response.getStatusLine().getStatusCode();
        assertEquals("Error code must be correct.", 404, result);
        assertEquals("Error message must be correct.", "ChatRoom with this id [999] not exists.", message);
    }

    @Test
    public void testSendPrivateMessage() throws IOException {
        UserDTO userDTO = new UserDTO("Serega", "serega@gmail.com", "pwd");
        CloseableHttpResponse httpResponse = register(userDTO, httpClient);
        UserDTO registeredUser = fromJson(contentToString(httpResponse), UserDTO.class);

        HttpPost httpPost = new HttpPost(SEND_PRIVATE_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, registeredUser.id, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = contentToString(response);
        MessageDTO messageDTO = fromJson(json, MessageDTO.class);
        assertEquals("Text messages must be equals.", messageRequest.text, messageDTO.text);
    }

    @Test
    public void testSendPrivateMessageToNotExistingUser() throws IOException {
        HttpPost httpPost = new HttpPost(SEND_PRIVATE_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, 999, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String message = contentToString(response);
        int result = response.getStatusLine().getStatusCode();
        assertEquals("Error code must be correct.", 404, result);
        assertEquals("Error message must be correct.", "User with this id [999] not exists.", message);
    }
}

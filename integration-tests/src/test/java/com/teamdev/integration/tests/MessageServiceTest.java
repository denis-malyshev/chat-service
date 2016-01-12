package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.utils.HttpResponseConverter;
import com.teamdev.utils.JsonHelper;
import com.teamdev.web.wrappers.MessageRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.teamdev.integration.tests.AuthenticationServiceTest.login;
import static com.teamdev.integration.tests.UserServiceTest.register;
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

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        httpClient = HttpClients.createDefault();
    }

    @Before
    public void setUp() throws Exception {
        token = login(TEST_LOGIN_INFO, httpClient);
    }

    @Test
    public void testSendMessageToExistingChat() throws IOException {
        HttpPost httpPost = new HttpPost(SEND_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, TEST_CHAT_ID.id, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(response);
        MessageDTO messageDTO = JsonHelper.fromJson(json, MessageDTO.class);
        assertEquals(messageRequest.text, messageDTO.text);
    }

    @Test
    public void testSendPrivateMessage() throws IOException {
        UserDTO userDTO = new UserDTO("Serega", "serega@gmail.com", "pwd");
        UserDTO registeredUser = register(userDTO, httpClient);

        HttpPost httpPost = new HttpPost(SEND_PRIVATE_URL);
        MessageRequest messageRequest = new MessageRequest(token, TEST_USER_ID, registeredUser.id, "Hello!");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(messageRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(response);
        MessageDTO messageDTO = JsonHelper.fromJson(json, MessageDTO.class);
        assertEquals(messageRequest.text, messageDTO.text);
    }
}

package com.teamdev.integration.tests;

import com.google.common.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.web.wrappers.ChatRoomRequest;
import com.teamdev.web.wrappers.UpdateChatRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static com.teamdev.integration.tests.AuthenticationServiceTest.login;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChatRoomServiceTest {

    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String CHAT_SERVICE_URL = HOME_URL + "/chat";
    private static final String CREATE_URL = CHAT_SERVICE_URL + "/create";
    private static final String FIND_ALL_URL = CHAT_SERVICE_URL + "/chats/all";
    private static final String JOIN_URL = CHAT_SERVICE_URL + "/join";
    private static final String DELETE_URL = CHAT_SERVICE_URL + "/delete";

    private static final LoginInfo TEST_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");
    private static final UserId TEST_USER_ID = new UserId(1);
    private static final ChatRoomId TEST_CHAT_ID = new ChatRoomId(1);
    private static final String TEST_CHAT_NAME = "test-chat";

    private static CloseableHttpClient httpClient;
    private Token token;

    @Before
    public void setUp() throws IOException {
        httpClient = HttpClients.createDefault();
        token = login(TEST_LOGIN_INFO, httpClient);
    }

    @Test
    public void testCreateChat() throws IOException {
        ChatRoomRequest chatRoomRequest = new ChatRoomRequest(token, TEST_USER_ID, "chat-1");
        ChatRoomDTO chatRoomDTO = create(chatRoomRequest, httpClient);
        assertEquals(chatRoomRequest.name, chatRoomDTO.name);
    }

    @Test
    public void testCreateChatWithExistingName() throws IOException {
        ChatRoomRequest chatRoomRequest = new ChatRoomRequest(token, TEST_USER_ID, TEST_CHAT_NAME);
        HttpPost httpPost = new HttpPost(CREATE_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(chatRoomRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String message = contentToString(response);
        int result = response.getStatusLine().getStatusCode();
        assertEquals("Error code must be correct.", 409, result);
        assertEquals("Error message must be correct.", format("ChatRoom %s already exists.", TEST_CHAT_NAME), message);
    }

    @Test
    public void testReadAllChats() throws IOException {
        HttpGet httpGet = new HttpGet(format("%s/?token=%s&userId=%d", FIND_ALL_URL, token.key, TEST_USER_ID.id));
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String json = contentToString(response);
        ArrayList<ChatRoomDTO> chatRoomDTOs = fromJson(json, new TypeToken<ArrayList<ChatRoomDTO>>() {
        }.getType());
        assertNotNull("ChatRooms must exists.", chatRoomDTOs);
    }

    @Test
    public void testJoinUserToChat() throws IOException {
        ChatRoomRequest chatRoomRequest = new ChatRoomRequest(token, TEST_USER_ID, "chat-2");
        ChatRoomDTO chatRoomDTO = create(chatRoomRequest, httpClient);
        UpdateChatRequest updateChatRequest = new UpdateChatRequest(
                token,
                TEST_USER_ID,
                new ChatRoomId(chatRoomDTO.id));

        HttpPut httpPut = new HttpPut(JOIN_URL);
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setEntity(new StringEntity(toJson(updateChatRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPut);
        String result = contentToString(response);
        assertEquals("Message must be correct.", "User successfully joined to chat.", result);
    }

    @Test
    public void testJoinUserToNotExistingChat() throws IOException {
        UpdateChatRequest updateChatRequest = new UpdateChatRequest(
                token,
                TEST_USER_ID,
                new ChatRoomId(999));

        HttpPut httpPut = new HttpPut(JOIN_URL);
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setEntity(new StringEntity(toJson(updateChatRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPut);
        String message = contentToString(response);
        int result = response.getStatusLine().getStatusCode();
        assertEquals("Error code must be correct.", 404, result);
        assertEquals("Error message must be correct.", "ChatRoom with this id [999] not exists.", message);
    }

    @Test
    public void testDeleteUserFromChat() throws IOException {
        UpdateChatRequest updateChatRequest = new UpdateChatRequest(
                token,
                TEST_USER_ID,
                TEST_CHAT_ID);

        HttpPut httpPut = new HttpPut(DELETE_URL);
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setEntity(new StringEntity(toJson(updateChatRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPut);
        String result = contentToString(response);
        assertEquals("Message must be correct.", "User successfully deleted from chat.", result);
    }

    public static ChatRoomDTO create(ChatRoomRequest chatRoomRequest, CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(CREATE_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(chatRoomRequest)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = contentToString(response);
        return fromJson(json, ChatRoomDTO.class);
    }
}

package com.teamdev.integration.tests;

import com.google.common.reflect.TypeToken;
import com.teamdev.chatservice.wrappers.dto.*;
import com.teamdev.chatservice.wrappers.ChatRoomRequest;
import com.teamdev.chatservice.wrappers.UpdateChatRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.teamdev.integration.tests.AuthenticationServiceTest.getTokenFromResponse;
import static com.teamdev.integration.tests.UserServiceTest.getUserFromResponse;
import static com.teamdev.integration.tests.UserServiceTest.register;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static java.lang.String.format;
import static org.junit.Assert.*;

public class ChatRoomServiceTest {
    private static final Logger LOG = Logger.getLogger(ChatRoomServiceTest.class);
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String CHAT_SERVICE_URL = HOME_URL + "/chat";
    private static final String CREATE_URL = CHAT_SERVICE_URL + "/create";
    private static final String FIND_ALL_URL = CHAT_SERVICE_URL + "/chats/all";
    private static final String JOIN_URL = CHAT_SERVICE_URL + "/join";
    private static final String DELETE_URL = CHAT_SERVICE_URL + "/delete";
    private static final Random RANDOM = new Random();

    private UserDTO testUserDTO;
    private UserId testUserId;
    private Token testToken;
    private ChatRoomDTO testChatDTO;
    private ChatRoomId testChatRoomId;
    private CloseableHttpClient httpClient;

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
        final int identifier = RANDOM.nextInt();
        String testUserEmail = format("userservice%d@gmail.com", identifier);
        String testChatRoomName = format("testChatForChatService%d", identifier);
        UserDTO userDTO = new UserDTO(
                "Vasya",
                testUserEmail,
                identifier + "");
        try {
            testUserDTO = getUserFromResponse(register(userDTO));
            testToken = getTokenFromResponse(AuthenticationServiceTest.login(
                    new LoginInfo(userDTO.email, userDTO.password)));
            testChatDTO = getChatFromResponse(create(
                    new ChatRoomRequest(testToken, new UserId(testUserDTO.id), testChatRoomName)));
            testUserId = new UserId(testUserDTO.id);
            testChatRoomId = new ChatRoomId(testChatDTO.id);
            joinUserToChat(testToken, testUserId, testChatRoomId);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void test_create_chat() {
        final int identifier = RANDOM.nextInt();
        String testChatName = "chat-" + identifier;
        try {
            ChatRoomRequest chatRoomRequest = new ChatRoomRequest(testToken, testUserId, testChatName);
            ChatRoomDTO chatRoomDTO = getChatFromResponse(create(chatRoomRequest));
            assertEquals(chatRoomRequest.name, chatRoomDTO.name);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_create_chat_with_existing_name() {
        try {
            ChatRoomRequest chatRoomRequest = new ChatRoomRequest(testToken, testUserId, testChatDTO.name);
            HttpPost httpPost = new HttpPost(CREATE_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(chatRoomRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 409, result);
            assertEquals("Error message must be correct.", format("ChatRoom %s already exists.", testChatDTO.name), message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_read_all_chats() {
        try {
            HttpGet httpGet = new HttpGet(format("%s/?token=%s&userId=%d", FIND_ALL_URL, testToken.key, testUserDTO.id));
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String json = contentToString(response);
            ArrayList<ChatRoomDTO> chatRoomDTOs = fromJson(json, new TypeToken<ArrayList<ChatRoomDTO>>() {
            }.getType());
            assertNotNull("ChatRooms must exists.", chatRoomDTOs);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_join_user_to_chat() {
        try {
            CloseableHttpResponse response = joinUserToChat(testToken, testUserId, testChatRoomId);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = contentToString(response);
            assertEquals(200, statusCode);
            assertEquals("Message must be correct.", "User successfully joined to chat.", result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_join_user_to_not_existing_chat() {
        try {
            CloseableHttpResponse response = joinUserToChat(testToken, testUserId, new ChatRoomId(2938456));
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 404, result);
            assertEquals("Error message must be correct.", "ChatRoom with this id [2938456] not exists.", message);
        } catch (IOException e) {
            fail();
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_delete_user_from_chat() {
        try {
            UpdateChatRequest updateChatRequest = new UpdateChatRequest(
                    testToken,
                    testUserId,
                    testChatRoomId);

            HttpPut httpPut = new HttpPut(DELETE_URL);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setEntity(new StringEntity(toJson(updateChatRequest)));

            CloseableHttpResponse response = httpClient.execute(httpPut);
            String result = contentToString(response);
            assertEquals("Message must be correct.", "User successfully deleted from chat.", result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    public static CloseableHttpResponse create(ChatRoomRequest chatRoomRequest) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(CREATE_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(chatRoomRequest)));

        return httpClient.execute(httpPost);
    }

    public static ChatRoomDTO getChatFromResponse(CloseableHttpResponse response) throws IOException {
        return fromJson(contentToString(response), ChatRoomDTO.class);
    }

    public static CloseableHttpResponse joinUserToChat(Token token, UserId userId, ChatRoomId chatRoomId) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        UpdateChatRequest updateChatRequest = new UpdateChatRequest(token, userId, chatRoomId);

        HttpPut httpPut = new HttpPut(JOIN_URL);
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setEntity(new StringEntity(toJson(updateChatRequest)));

        return httpClient.execute(httpPut);
    }
}

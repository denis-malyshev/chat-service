package com.teamdev.integration.tests;

import com.google.gson.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chatservice.wrappers.ChatRoomRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.Random;

import static com.teamdev.integration.tests.AuthenticationServiceTest.getTokenFromResponse;
import static com.teamdev.integration.tests.ChatRoomServiceTest.*;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static java.lang.String.format;
import static org.junit.Assert.*;

public class UserServiceTest {
    private static final Logger LOG = Logger.getLogger(UserServiceTest.class);
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String USER_SERVICE_URL = HOME_URL + "/user";
    private static final String FIND_URL = USER_SERVICE_URL + "/find";
    private static final String REGISTER_URL = USER_SERVICE_URL + "/register";
    private static final String FIND_CHATS_URL = USER_SERVICE_URL + "/chats";
    private static final Random RANDOM = new Random();
    private static UserDTO testUserDTO;
    private static Token testToken;
    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void beforeClass() {
        final int identifier = RANDOM.nextInt();
        String testUserEmail = format("userservice%d@gmail.com", identifier);
        String testChatRoomName = format("testChatForUserService%d", identifier);
        try {
            UserDTO userDTO = new UserDTO(
                    "VasyaFromUserService",
                    testUserEmail,
                    identifier + "");
            testUserDTO = getUserFromResponse(register(userDTO));
            testToken = getTokenFromResponse(AuthenticationServiceTest.login(
                    new LoginInfo(userDTO.email, userDTO.password)));
            ChatRoomDTO testChatDTO = getChatFromResponse(create(
                    new ChatRoomRequest(testToken, new UserId(testUserDTO.id), testChatRoomName)));
            joinUserToChat(testToken, new UserId(testUserDTO.id), new ChatRoomId(testChatDTO.id));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void test_register_user() {
        final int identifier = RANDOM.nextInt();
        String testEmail = format("vasya%d@gmail.com", identifier);
        UserDTO registerDTO = new UserDTO("Vasya", testEmail, identifier + "");
        try {
            CloseableHttpResponse response = register(registerDTO);
            UserDTO userDTO = getUserFromResponse(response);
            assertEquals("Emails must be equals.", registerDTO.email, userDTO.email);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_register_exists_user() {
        try {
            UserDTO registerDTO = new UserDTO("Vasya", "vasya@gmail.com", "pwd");
            register(registerDTO);
            CloseableHttpResponse response = register(registerDTO);
            int statusCode = response.getStatusLine().getStatusCode();
            String message = contentToString(response);
            assertEquals(403, statusCode);
            assertEquals("Error message must be correct.", "User with the same mail already exists.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_register_user_with_incorrect_email() {
        try {
            UserDTO registerDTO = new UserDTO("Vasya", "vasya-gmail.com", "pwd");
            CloseableHttpResponse response = register(registerDTO);
            int result = response.getStatusLine().getStatusCode();
            String message = contentToString(response);
            assertEquals("Error code must be correct.", 409, result);
            assertEquals("Error message must be correct.", "Enter a correct email.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_find_by_id() {
        final int identifier = RANDOM.nextInt();
        String testEmail = format("masha%d@gmail.com", identifier);
        try {
            UserDTO registerDTO = new UserDTO("Masha", testEmail, "pwd");
            CloseableHttpResponse httpResponse = register(registerDTO);
            UserDTO registeredDTO = fromJson(contentToString(httpResponse), UserDTO.class);

            HttpGet httpGet = new HttpGet(format("%s/%d?token=%s&userId=%d", FIND_URL, registeredDTO.id, testToken.key, testUserDTO.id));
            CloseableHttpResponse response = httpClient.execute(httpGet);
            UserDTO userDTO = fromJson(contentToString(response), UserDTO.class);
            assertEquals("UserDTOs must be equals.", registeredDTO, userDTO);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_find_by_id_not_existing_user() {
        try {
            HttpGet httpGet = new HttpGet(format("%s/%d?token=%s&userId=%d", FIND_URL, 999, testToken.key, testUserDTO.id));
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String message = contentToString(response);
            assertEquals("Error code must be correct", 404, statusCode);
            assertEquals("Error message must be correct.", "User with id[999] not exists.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_find_available_chats() {
        try {
            HttpGet httpGet = new HttpGet(format("%s/?token=%s&userId=%d", FIND_CHATS_URL, testToken.key, testUserDTO.id));
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String json = contentToString(response);
            LOG.info(json);
            ArrayList<ChatRoomDTO> availableChats = fromJson(json, new TypeToken<ArrayList<ChatRoomDTO>>() {
            }.getType());

            assertNotNull("ChatRooms must exists.", availableChats);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    public static CloseableHttpResponse register(UserDTO userDTO) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(REGISTER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(userDTO)));

        return httpClient.execute(httpPost);
    }

    public static UserDTO getUserFromResponse(CloseableHttpResponse response) throws IOException {
        return fromJson(contentToString(response), UserDTO.class);
    }
}

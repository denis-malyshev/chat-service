package com.teamdev.integration.tests;

import com.google.gson.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.utils.HttpResponseConverter;
import com.teamdev.utils.JsonHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.teamdev.integration.tests.AuthenticationServiceTest.login;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserServiceTest {

    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String USER_SERVICE_URL = HOME_URL + "/user";
    private static final String FIND_URL = USER_SERVICE_URL + "/find";
    private static final String REGISTER_URL = USER_SERVICE_URL + "/register";
    private static final String FIND_CHATS_URL = USER_SERVICE_URL + "/chats";
    private static final LoginInfo TEST_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");
    private static final int TEST_USER_ID = 1;

    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void setUp() throws URISyntaxException {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testRegisterUser() throws IOException {
        UserDTO registerDTO = new UserDTO("Vasya", "vasya@gmail.com", "pwd");
        UserDTO userDTO = register(registerDTO, httpClient);
        assertEquals(registerDTO.email, userDTO.email);
    }

    @Test
    public void testFindById() throws IOException {
        UserDTO registerDTO = new UserDTO("Masha", "masha@gmail.com", "pwd");
        UserDTO registeredDTO = register(registerDTO, httpClient);

        Token token = login(new LoginInfo(registerDTO.email, registerDTO.password), httpClient);

        HttpGet httpGet = new HttpGet(format("%s/%d?token=%s&userId=%d", FIND_URL, registeredDTO.id, token.key, registeredDTO.id));
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String json = HttpResponseConverter.contentToString(response);
        UserDTO userDTO = JsonHelper.fromJson(json, UserDTO.class);
        assertEquals(registeredDTO, userDTO);
    }

    @Test
    public void testFindAvailableChats() throws Exception {
        Token token = login(TEST_LOGIN_INFO, httpClient);

        HttpGet httpGet = new HttpGet(format("%s/?token=%s&userId=%d", FIND_CHATS_URL, token.key, TEST_USER_ID));
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String json = HttpResponseConverter.contentToString(response);
        ArrayList<ChatRoomDTO> availableChats = JsonHelper.fromJson(json, new TypeToken<ArrayList<ChatRoomDTO>>() {
        }.getType());

        assertNotNull(availableChats);
    }

    public static UserDTO register(UserDTO userDTO, CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(REGISTER_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(userDTO)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(response);
        return JsonHelper.fromJson(json, UserDTO.class);
    }
}

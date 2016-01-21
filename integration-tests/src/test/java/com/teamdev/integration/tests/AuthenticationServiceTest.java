package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static com.teamdev.integration.tests.UserServiceTest.getUserFromResponse;
import static com.teamdev.integration.tests.UserServiceTest.register;
import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static java.lang.String.format;
import static org.junit.Assert.*;

public class AuthenticationServiceTest {
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String AUTHENTICATION_SERVICE_URL = HOME_URL + "/auth";
    private static final String LOGIN_URL = AUTHENTICATION_SERVICE_URL + "/login";
    private static final String LOGOUT_URL = AUTHENTICATION_SERVICE_URL + "/logout";
    private static final Random RANDOM = new Random();

    private static LoginInfo validLoginInfo = new LoginInfo("vasya1@gmail.com", "pwd");
    private static LoginInfo invalidLoginInfo = new LoginInfo("vasya1@gmail.com", "password123");

    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void beforeClass() {
        final int identifier = RANDOM.nextInt();
        String testUserEmail = format("authservice%d@gmail.com", identifier);
        UserDTO userDTO = new UserDTO(
                "VasyaFromAuthService",
                testUserEmail,
                identifier + "");
        try {
            validLoginInfo = new LoginInfo(userDTO.email, userDTO.password);
            getUserFromResponse(register(userDTO));
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void test_login() {
        try {
            Token token = getTokenFromResponse(login(validLoginInfo));
            assertNotNull("Token must exists.", token);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_logout() {
        try {
            Token token = getTokenFromResponse(login(validLoginInfo));
            String result = logout(token.key);
            assertEquals("Message must be correct.", "successfully", result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    @Test
    public void test_login_with_invalid_password() {
        try {
            HttpPost httpPost = new HttpPost(LOGIN_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(invalidLoginInfo)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 403, result);
            assertEquals("Error message must be correct.", "Invalid login or password.", message);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }

    public static CloseableHttpResponse login(LoginInfo loginInfo) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(loginInfo)));

        return httpClient.execute(httpPost);
    }

    public static Token getTokenFromResponse(CloseableHttpResponse response) throws IOException {
        return fromJson(contentToString(response), Token.class);
    }

    private String logout(String token) throws IOException {
        HttpDelete httpDelete = new HttpDelete(LOGOUT_URL + "/?token=" + token);

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        return contentToString(response);
    }
}

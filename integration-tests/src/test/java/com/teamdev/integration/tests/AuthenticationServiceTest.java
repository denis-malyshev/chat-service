package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationServiceTest {
    private static final Logger LOG = Logger.getLogger(AuthenticationServiceTest.class);
    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String AUTHENTICATION_SERVICE_URL = HOME_URL + "/auth";
    private static final String LOGIN_URL = AUTHENTICATION_SERVICE_URL + "/login";
    private static final String LOGOUT_URL = AUTHENTICATION_SERVICE_URL + "/logout";
    private static final LoginInfo VALID_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");
    private static final LoginInfo INVALID_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "password123");

    private static CloseableHttpClient httpClient;

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testLogin() {
        try {
            Token token = getTokenFromResponse(login(VALID_LOGIN_INFO));
            assertNotNull("Token must exists.", token);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testLogout() {
        try {
            Token token = getTokenFromResponse(login(VALID_LOGIN_INFO));
            String result = logout(token.key);
            assertEquals("Message must be correct.", "successfully", result);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testLoginWithInvalidPassword() {
        try {
            HttpPost httpPost = new HttpPost(LOGIN_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(toJson(INVALID_LOGIN_INFO)));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String message = contentToString(response);
            int result = response.getStatusLine().getStatusCode();
            assertEquals("Error code must be correct.", 403, result);
            assertEquals("Error message must be correct.", "Invalid login or password.", message);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
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

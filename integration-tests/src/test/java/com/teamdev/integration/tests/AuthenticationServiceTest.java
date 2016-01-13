package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static com.teamdev.utils.JsonHelper.fromJson;
import static com.teamdev.utils.JsonHelper.toJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationServiceTest {

    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String AUTHENTICATION_SERVICE_URL = HOME_URL + "/auth";
    private static final String LOGIN_URL = AUTHENTICATION_SERVICE_URL + "/login";
    private static final String LOGOUT_URL = AUTHENTICATION_SERVICE_URL + "/logout";
    private static final LoginInfo VALID_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");
    private static final LoginInfo INVALID_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "password123");

    private static CloseableHttpClient httpClient;

    @Before
    public void setUp() throws URISyntaxException {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testLogin() throws IOException {
        Token token = login(VALID_LOGIN_INFO, httpClient);
        System.out.println("token = " + token.key);
        assertNotNull("Token must exists.", token);
    }

    @Test
    public void testLogout() throws IOException {
        Token token = login(VALID_LOGIN_INFO, httpClient);
        String result = logout(token.key);
        assertEquals("Message must be correct.", "successfully", result);
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(INVALID_LOGIN_INFO)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String message = contentToString(response);
        int result = response.getStatusLine().getStatusCode();
        assertEquals("Error code must be correct.", 403, result);
        assertEquals("Error message must be correct.", "Invalid login or password.", message);
    }

    public static Token login(LoginInfo loginInfo, CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(toJson(loginInfo)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = contentToString(response);
        return fromJson(json, Token.class);
    }

    private String logout(String token) throws IOException {
        HttpDelete httpDelete = new HttpDelete(LOGOUT_URL + "/?token=" + token);

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        return contentToString(response);
    }
}

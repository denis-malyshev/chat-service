package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.utils.HttpResponseConverter;
import com.teamdev.utils.JsonHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationServiceTest {

    private static final String HOME_URL = "http://localhost:8080/chat-service";
    private static final String AUTHENTICATION_SERVICE_URL = HOME_URL + "/auth";
    private static final String LOGIN_URL = AUTHENTICATION_SERVICE_URL + "/login";
    private static final String LOGOUT_URL = AUTHENTICATION_SERVICE_URL + "/logout";
    private static final LoginInfo VALID_LOGIN_INFO = new LoginInfo("vasya1@gmail.com", "pwd");

    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void setUp() throws URISyntaxException {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testLogin() throws IOException {
        Token token = login(VALID_LOGIN_INFO, httpClient);
        assertNotNull(token);
    }

    @Test
    public void testLogout() throws IOException {
        Token token = login(VALID_LOGIN_INFO, httpClient);
        String result = logout(token.key);
        assertEquals("successfully", result);
    }

    public static Token login(LoginInfo loginInfo, CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(loginInfo)));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(response);
        return JsonHelper.fromJson(json, Token.class);
    }

    private String logout(String token) throws IOException {
        HttpDelete httpDelete = new HttpDelete(LOGOUT_URL + "/?token=" + token);

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        return HttpResponseConverter.contentToString(response);
    }
}

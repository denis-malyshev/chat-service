package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.LoginInfo;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.utils.HttpResponseConverter;
import com.teamdev.utils.JsonHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class AuthenticationServiceTest {

    private static final String HOME_URL = "http://localhost:8080";
    private CloseableHttpClient httpClient;

    @Before
    public void setUp() throws URISyntaxException {

        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testLogin() throws Exception {
        Token token = login(new LoginInfo("vasya@gmai.com", "pwd"));
        assertNotNull(token);
    }

    private Token login(LoginInfo loginInfo) throws IOException {
        HttpPost httpPost = new HttpPost(HOME_URL + "/login");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(loginInfo)));

        CloseableHttpResponse execute = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(execute);
        return JsonHelper.fromJson(json, Token.class);
    }
}

package com.teamdev.integration.tests;

import com.teamdev.chat.service.impl.dto.UserDTO;
import com.teamdev.utils.HttpResponseConverter;
import com.teamdev.utils.JsonHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    private static final String HOME_URL = "http://localhost:8080";
    private CloseableHttpClient httpClient;

    @Before
    public void setUp() throws URISyntaxException {

        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testRegisterUser() throws IOException {
        HttpPost httpPost = new HttpPost(HOME_URL + "/user/register");
        UserDTO userDTO = new UserDTO("Vasya", "vasya@gmail.com", "pwd");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JsonHelper.toJson(userDTO)));

        CloseableHttpResponse execute = httpClient.execute(httpPost);
        String json = HttpResponseConverter.contentToString(execute);
        UserDTO registeredDTO = JsonHelper.fromJson(json, UserDTO.class);
        assertEquals(userDTO.email, registeredDTO.email);
    }

    @Test
    public void testFindById() throws Exception {
        HttpGet httpGet = new HttpGet(HOME_URL + "user/find");
    }
}

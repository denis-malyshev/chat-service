package com.teamdev.integration.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    CloseableHttpClient httpClient;
    HttpGet request;
    String validTokenKey;
    final long validUserId = 1;

    @Before
    public void setUp() throws Exception {

        httpClient = HttpClients.createDefault();
        validTokenKey = String.valueOf(LocalDateTime.now().getDayOfYear()) + validUserId;
    }

    @Test
    public void testPositiveGetToURL_shouldReturnProperJSON() throws Exception {

        request = new HttpGet(String.format("http://localhost:8080/chats?token=%s&userId=%d", validTokenKey, validUserId));
        HttpResponse response = httpClient.execute(request);

        String json = contentToString(response);

        Set<ChatRoomDTO> chatRoomDTOs = fromJson(json);

        int result = chatRoomDTOs.size();

        assertEquals("The count of chat-rooms must be 1.", 1, result);
    }

    @Test
    public void testNegativeGetToURL_shouldReturnErrorCode_403() throws Exception {

        request = new HttpGet("http://localhost:8080/chats?token=999&userId=999");
        HttpResponse response = httpClient.execute(request);
        response.getEntity().getContentType().getValue();
        int result = response.getStatusLine().getStatusCode();
        assertEquals(403, result);
    }

    private String contentToString(HttpResponse response) {
        byte[] bytes = new byte[0];
        try {
            InputStream inputStream = response.getEntity().getContent();
            bytes = toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }

    private Set<ChatRoomDTO> fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Set<ChatRoomDTO>>() {
        }.getType());
    }
}

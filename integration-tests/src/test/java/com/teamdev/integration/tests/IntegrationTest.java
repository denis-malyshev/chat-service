package com.teamdev.integration.tests;

import com.google.gson.reflect.TypeToken;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.utils.JsonHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.teamdev.utils.HttpResponseConverter.contentToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntegrationTest {

    private CloseableHttpClient httpClient;
    private HttpGet request;
    private String validTokenKey;
    private static final long validUserId = 1;

    @Before
    public void setUp() throws URISyntaxException {

        httpClient = HttpClients.createDefault();
        validTokenKey = String.valueOf(LocalDateTime.now().getDayOfYear()) + validUserId;
    }

    @Test
    public void testPositiveGetToURL_shouldReturnProperJSON() {
        request = new HttpGet(String.format("http://localhost:8080/webapp-1.0/chats?token=%s&userId=%d", validTokenKey, validUserId));
        try {
            HttpResponse response = httpClient.execute(request);
            String json = contentToString(response);

            ArrayList<ChatRoomDTO> chatRoomDTOs = JsonHelper.fromJson(json, new TypeToken<ArrayList<ChatRoomDTO>>() {
            }.getType());

            int result = chatRoomDTOs.size();
            assertEquals("The count of chat-rooms must be 1.", 1, result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }

    }

    @Test
    public void testNegativeGetToURL_shouldReturnErrorCode_403() {
        request = new HttpGet("http://localhost:8080/webapp-1.0/chats?token=999&userId=999");
        try {
            HttpResponse response = httpClient.execute(request);
            response.getEntity().getContentType().getValue();

            int result = response.getStatusLine().getStatusCode();
            assertEquals(403, result);
        } catch (IOException e) {
            fail("Unexpected exception.");
        }
    }
}

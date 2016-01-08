package com.teamdev.utils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;

public final class HttpResponseConverter {

    public static String contentToString(HttpResponse response) throws IOException {
        InputStream inputStream = response.getEntity().getContent();
        return new String(toByteArray(inputStream));
    }
}

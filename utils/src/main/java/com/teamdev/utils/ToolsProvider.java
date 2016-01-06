package com.teamdev.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import static com.google.common.io.ByteStreams.toByteArray;

public class ToolsProvider {

    private static final Gson GSON = new Gson();
    private static final HashFunction hashFunction = Hashing.md5();

    public static String toJson(Object data) {
        return GSON.toJson(data);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static String passwordHash(String password) {
        return hashFunction.newHasher().
                putString(password, Charset.defaultCharset()).
                hash().toString();
    }

    public static String contentToString(HttpResponse response) {
        try {
            InputStream inputStream = response.getEntity().getContent();
            return new String(toByteArray(inputStream));
        } catch (IOException e) {
            return "";
        }
    }
}

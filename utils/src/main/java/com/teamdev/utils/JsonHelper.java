package com.teamdev.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

public final class JsonHelper {

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
}

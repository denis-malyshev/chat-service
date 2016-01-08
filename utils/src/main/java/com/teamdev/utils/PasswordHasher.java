package com.teamdev.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class PasswordHasher {

    private static final HashFunction hashFunction = Hashing.md5();

    public static String createHash(String password) {
        return hashFunction.newHasher().
                putString(password, Charset.defaultCharset()).
                hash().toString();
    }
}

package com.teamdev.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Random;

public class Hasher {

    private static final HashFunction HASH_FUNCTION = Hashing.md5();
    private static final Random RANDOM = new Random();

    public static String createHash(String toHash) {
        return HASH_FUNCTION.newHasher().
                putString(toHash, Charset.defaultCharset()).
                hash().toString();
    }

    public static String modification(String hash) {
        char[] chars = hash.toCharArray();
        char temp;
        for (int i = 0; i < chars.length; i++) {
            int index = RANDOM.nextInt(chars.length);
            temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }
        return String.valueOf(chars);
    }
}

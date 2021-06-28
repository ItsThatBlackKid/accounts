package com.saokanneh.auth.shared;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {

    private Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;

    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnVal = new StringBuilder(length);

        for(int i = 0; i < length; i++) {
            returnVal.append(ALPHABET.charAt(RANDOM.nextInt((ALPHABET.length()))));
        }

        return new String(returnVal);
    }
}

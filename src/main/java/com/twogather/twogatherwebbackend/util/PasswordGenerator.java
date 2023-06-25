package com.twogather.twogatherwebbackend.util;

import com.twogather.twogatherwebbackend.exception.MemberException;

import java.security.SecureRandom;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.PASSWORD_GENERATOR_EXCEPTION;

public class PasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generatePassword(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        String password = builder.toString();

        if (password.matches("^(?=.*[0-9])(?=.*[a-z]).{8,20}$")) {
            return password;
        } else {
            throw new MemberException(PASSWORD_GENERATOR_EXCEPTION);
        }
    }

    public static int getRandomNumberBetween(int a, int b) {
        if (b < a) {
            throw new MemberException(PASSWORD_GENERATOR_EXCEPTION);
        }
        return RANDOM.nextInt(b - a + 1) + a;
    }
}

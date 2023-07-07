package com.twogather.twogatherwebbackend.util;

import com.twogather.twogatherwebbackend.exception.MemberException;

import java.security.SecureRandom;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.PASSWORD_GENERATOR_EXCEPTION;

public class PasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generatePassword(int length) {
        StringBuilder builder = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (int i = 0; i < length; i++) {
            int charType = random.nextInt(3);

            if (charType == 0) {
                builder.append(lowercase.charAt(random.nextInt(lowercase.length())));
                hasLowercase = true;
            } else if (charType == 1) {
                builder.append(digits.charAt(random.nextInt(digits.length())));
                hasDigit = true;
            } else {
                builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
        }

        if (hasLowercase && hasDigit) {
            return builder.toString();
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

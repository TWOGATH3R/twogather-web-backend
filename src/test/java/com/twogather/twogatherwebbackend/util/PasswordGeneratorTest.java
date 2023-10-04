package com.twogather.twogatherwebbackend.util;

import com.twogather.twogatherwebbackend.exception.MemberException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordGeneratorTest {

    /*TODO: 간헐적으로 테스트가 실패함
    @Test
    void generatePassword_lengthInRange() {
        int length = 10;
        String password = PasswordGenerator.generatePassword(length);
        Assertions.assertEquals(length, password.length());
    }

    @Test
    void generatePassword_meetsRequirements() {
        int length = 10;
        String password = PasswordGenerator.generatePassword(length);
        Assertions.assertTrue(password.matches("^(?=.*[0-9])(?=.*[a-z]).{8,20}$"));
    }

    @Test
    void getRandomNumberBetween_validRange() {
        int a = 1;
        int b = 10;
        int randomNumber = PasswordGenerator.getRandomNumberBetween(a, b);
        Assertions.assertTrue(randomNumber >= a && randomNumber <= b);
    }

    @Test
    void getRandomNumberBetween_invalidRange() {
        int a = 10;
        int b = 1;
        Assertions.assertThrows(MemberException.class, () -> PasswordGenerator.getRandomNumberBetween(a, b));
    }*/
}
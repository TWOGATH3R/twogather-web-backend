package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SystemTest {

    @Autowired
    private BizRegNumberValidator validator;

    private static final String BIZ_REG_NUMBER = "0000000000";
    private static final String BIZ_START_DATE = "20000101";
    private static final String BIZ_NAME = "홍길동";

    @Test
    public void validateBizRegNumber_InvalidBizRegNumber_ReturnsFalse() {
        boolean isValid = validator.validateBizRegNumber(BIZ_REG_NUMBER,BIZ_START_DATE,BIZ_NAME);
        Assertions.assertFalse(isValid, "올바르지 않은 사업자 등록번호의 경우 유효하지 않은 값이 반환되어야 합니다.");
    }
}

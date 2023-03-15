package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SystemTest {

    static final String bizRegNumber = "0000000000";
    static final String bizStartDate = "20000101";
    static final String bizName = "홍길동";

    @Test
    public void isValidate() throws JSONException {
        boolean isValid = BizRegNumberValidator.validateBizRegNumber(bizRegNumber,bizStartDate,bizName);
        Assertions.assertFalse(isValid);
    }
}

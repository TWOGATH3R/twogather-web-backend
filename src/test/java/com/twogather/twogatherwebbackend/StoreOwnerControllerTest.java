package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StoreOwnerControllerTest extends AcceptanceTest{

    private StoreOwnerSaveRequest returnRequest(){
        return new StoreOwnerSaveRequest(
                "test@test.com",
                "test",
                "김사업",
                "010-1234-1234",
                "0000000000",
                "김사업이름",
                "20200101"
        );
    }
}


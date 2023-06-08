package com.twogather.twogatherwebbackend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.Response;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

public class TestUtil {
    public static <T> T convert(Response response, TypeReference<T> typeReference){
        return new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(response.getData(), typeReference);
    }
}

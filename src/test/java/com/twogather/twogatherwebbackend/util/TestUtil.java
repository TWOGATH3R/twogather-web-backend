package com.twogather.twogatherwebbackend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.common.Response;

public class TestUtil {
    public static <T> T convert(Response response, TypeReference<T> typeReference){
        return new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(response.getData(), typeReference);
    }

}

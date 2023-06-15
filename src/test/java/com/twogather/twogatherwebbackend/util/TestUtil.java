package com.twogather.twogatherwebbackend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.dto.Response;

import java.util.Date;

public class TestUtil {
    public static <T> T convert(Response response, TypeReference<T> typeReference){
        return new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(response.getData(), typeReference);
    }

}

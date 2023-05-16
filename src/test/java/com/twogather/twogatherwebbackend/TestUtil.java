package com.twogather.twogatherwebbackend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

public class TestUtil {
    public static <T> T convert(MvcResult result, TypeReference typeReference) throws Exception {
        return (T) new ObjectMapper().readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), typeReference);
    }
}

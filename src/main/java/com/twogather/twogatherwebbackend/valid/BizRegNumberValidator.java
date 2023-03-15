package com.twogather.twogatherwebbackend.valid;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;


@Slf4j
@Component
public class BizRegNumberValidator {
    private final String url;
    private final String key;
    private final String totalUrl;

    public BizRegNumberValidator(@Value("${api.validate.url}") String url,
                                 @Value("${api.validate.service.key}") String key) {
        Objects.requireNonNull(url, "url must not be null");
        Objects.requireNonNull(key, "key must not be null");
        this.url = url;
        this.key = key;
        this.totalUrl = this.url + "?serviceKey=" + this.key;
    }

    public boolean validateBizRegNumber(String bizRegNumber, String bizStartDate, String bizName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = makeJsonString(bizRegNumber, bizStartDate, bizName);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(totalUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();
            return isValid(responseBody);
        } catch (HttpServerErrorException e) {
            log.error("Failed to validate business registration number: " + e.getResponseBodyAsString(), e);
            return false;
        }
    }

    private boolean isValid(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray dataArray = jsonResponse.optJSONArray("data");
            if (dataArray == null || dataArray.length()==0) {
                return false;
            }
            JSONObject dataObject = dataArray.getJSONObject(0);
            String valid = dataObject.optString("valid");
            return "01".equals(valid);
        } catch (JSONException e) {
            log.error("Failed to validate business registration number: " + e.getMessage(), e);
            return false;
        }
    }

    private String makeJsonString(String bizRegNumber, String bizStartDate, String bizName) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Collections.singletonMap("businesses", Collections.singletonList(
                    new JSONObject()
                            .put("b_no", bizRegNumber)
                            .put("start_dt", bizStartDate)
                            .put("p_nm", bizName)
            )));
        } catch (JSONException e) {
            log.error("Failed to validate business registration number: " + e.getMessage(), e);
            return null;
        }
        return jsonObject.toString();
    }
}
package com.twogather.twogatherwebbackend.valid;

import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;


@Slf4j
@Component
@RequiredArgsConstructor
public class BizRegNumberValidator{
    private final PrivateConstants constants;

    public boolean validateBizRegNumber(final String bizRegNumber, final LocalDate bizStartDate, final String bizName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String totalUrl = constants.API_URL + "?serviceKey=" + constants.API_KEY;
        String requestJson = makeJsonString(bizRegNumber, bizStartDate, bizName);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(totalUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();
            return isValidResponse(responseBody);
        } catch (HttpServerErrorException e) {
            log.error("Failed to validate business registration number: " + e.getResponseBodyAsString(), e);
            return false;
        }
    }

    public boolean isValidResponse(final String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray dataArray = jsonResponse.optJSONArray("data");
            if (dataArray == null || dataArray.length() == 0) {
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

    private String makeJsonString(final String bizRegNumber, final LocalDate bizStartDate, final String bizName) {
        JSONObject jsonObject = new JSONObject(Collections.singletonMap("businesses", Collections.singletonList(
                new JSONObject()
                        .put("b_no", bizRegNumber)
                        .put("start_dt", localDateToString(bizStartDate))
                        .put("p_nm", bizName)
        )));
        return jsonObject.toString();
    }

    public String localDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


}
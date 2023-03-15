package com.twogather.twogatherwebbackend.valid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class BizRegNumberValidator{
    private static final String API_URL = "https://api.odcloud.kr/api/nts-businessman/v1/validate";
    private static final String SERVICE_KEY = "SabGDv9Xeo/2NOeWJkp/XxZu4if9/dwIhFWQiPvSGc8b9inh2SGBHtxpTRheIjznYul8sQdB7UYAO4qf6o0nlw==";
    private static final String TOTAL_URL = API_URL + "?serviceKey=" + SERVICE_KEY;
    public static boolean validateBizRegNumber(String bizRegNumber, String bizStartDate, String bizName) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = makeJsonString(bizRegNumber, bizStartDate, bizName);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(TOTAL_URL, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();
        return isValid(responseBody);

    }
    private static boolean isValid(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray dataArray = jsonResponse.getJSONArray("data");
        JSONObject dataObject = dataArray.getJSONObject(0);
        String valid = dataObject.getString("valid");
        return valid.equals("01");
    }
    private static String makeJsonString(String b_no, String start_dt, String p_nm) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject businessObject = new JSONObject();

        try {
            businessObject.put("b_no", b_no);
            businessObject.put("start_dt", start_dt);
            businessObject.put("p_nm", p_nm);

            jsonArray.put(businessObject);
            jsonObject.put("businesses", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}

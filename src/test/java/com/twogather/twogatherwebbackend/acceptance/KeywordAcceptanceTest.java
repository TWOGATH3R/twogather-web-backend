package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import com.twogather.twogatherwebbackend.repository.KeywordRepository;
import com.twogather.twogatherwebbackend.repository.StoreKeywordRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeywordAcceptanceTest extends AcceptanceTest{
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private StoreKeywordRepository storeKeywordRepository;

    private String URL = "/api/keywords";
    private String keywordName1 = "저렴한";
    private String keywordName2 = "분위기 좋은";
    private String keywordName3 = "가족들과 오기 좋은";

    @Test
    @DisplayName("다섯개보다 많은 키워드가 있을때 다섯개의 키워드만 랜덤으로 조회해온다")
    public void whenExistKeywordMoreThanFive_ThenRandomKeywordList() {
        createMoreThanFive();

        given()
                .when()
                .get(URL)
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.size()", equalTo(5))
                .body("data.name", notNullValue())
                .body("data.keywordId", notNullValue());

    }

    @Test
    @DisplayName("다섯개보다 적은 키워드가 있을때 해당 개수의 키워드만 랜덤으로 조회해온다")
    public void whenExistKeywordLessThanFive_ThenRandomKeywordList() {
        createLessThanFive();

        given()
                .when()
                .get(URL)
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.size()", equalTo(2))
                .body("data.name", notNullValue())
                .body("data.keywordId", notNullValue());

    }

    @Test
    @DisplayName("가게의 키워드 세개 설정")
    public void whenSettingThreeKeywordsForStore_thenTheyAreSavedInStoreKeywordTable()  {
        //given
        registerOwner();
        registerStore();
        approveStore();

        //when
        List<String> request = createRequest();
        doPut(URL+"/stores/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                request)
                .statusCode(HttpStatus.OK.value());
        //then
        Long keywordId1 = keywordRepository.findByName(keywordName1).get().getKeywordId();
        Long keywordId2 = keywordRepository.findByName(keywordName2).get().getKeywordId();
        Long keywordId3 = keywordRepository.findByName(keywordName3).get().getKeywordId();

        List<StoreKeyword> storeKeywordList = storeKeywordRepository.findByStoreStoreId(storeId);
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getKeywordId().equals(keywordId1)));
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getKeywordId().equals(keywordId2)));
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getKeywordId().equals(keywordId3)));

    }

    @Test
    @DisplayName("가게의 키워드 세개이상으로 입력이 들어온경우 throw exception")
    public void whenSettingTwoKeywordsForStore_thenThrowException()  {
        //given
        registerOwner();
        registerStore();
        approveStore();

        List<String> request = createMoreThan3Request();
        doPut(URL+ "/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("정해진 개수만큼의 키워드만 입력할 수 있습니다"));

    }

    @Test
    @DisplayName("가게의 키워드가 빈 값이 들어온 경우 무시한다")
    public void whenEmptyKeyword_thenIgnore() {
        //given
        registerOwner();
        registerStore();
        approveStore();

        int keywordSize = 2;

        doPut(URL+ "/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                createEmptyRequest())
                .statusCode(HttpStatus.OK.value());

        List<StoreKeyword> storeKeywordList = storeKeywordRepository.findByStoreStoreId(storeId);
        Assertions.assertEquals(storeKeywordList.size(), keywordSize);
        Assertions.assertTrue(storeKeywordRepository.findKeywordsByStoreId(storeId).stream().anyMatch(k -> k.getName().equals(createEmptyRequest().get(0))));
        Assertions.assertTrue(storeKeywordRepository.findKeywordsByStoreId(storeId).stream().anyMatch(k -> k.getName().equals(createEmptyRequest().get(1))));

    }

    private void createMoreThanFive(){
        keywordRepository.save(new Keyword("keyword1"));
        keywordRepository.save(new Keyword("keyword2"));
        keywordRepository.save(new Keyword("keyword3"));
        keywordRepository.save(new Keyword("keyword4"));
        keywordRepository.save(new Keyword("keyword5"));
        keywordRepository.save(new Keyword("keyword6"));
    }
    private void createLessThanFive(){
        keywordRepository.save(new Keyword("keyword1"));
        keywordRepository.save(new Keyword("keyword2"));
    }
    private List createRequest(){
        return new ArrayList<>(){{
            add(keywordName1);
            add(keywordName2);
            add(keywordName3);
        }};
    }
    private List createEmptyRequest(){
        return new ArrayList<>(){{
            add(keywordName1);
            add(keywordName2);
            add("");
        }};
    }
    private List createMoreThan3Request(){
        return new ArrayList<>(){{
            add(keywordName1);
            add(keywordName2);
            add(keywordName3);
            add(keywordName3);
        }};
    }
}

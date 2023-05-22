package com.twogather.twogatherwebbackend;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class KeywordGeneratorTest {
    KeywordGenerator generator = new KeywordGenerator();

    @Test
    void test(){
        List<String> reviews = new ArrayList<>();
        reviews.add("정말 음식이 맛있고 멋있는 맛이죠");
        reviews.add("친구들이랑 오기좋은 곳 같아요. 예쁘게 생겻어요 귀엽게 생겼어요");
        reviews.add("밥도 맛있고, 재밌는것같아요, 예쁘게 생겼어요");
        reviews.add("힙리적인 가격으로 소비할 수 있었어요. 이벤트도 있더라구요");
        reviews.add("합리적인 가격으로 소비할 수 있었어요. 맛있어요. 오기좋아요");
        reviews.add("저렴한 가격에 이용할 수 있었어요. 공부하기 좋아요");
        reviews.add("분위기가 좋아요. 아이들과 같이 오기 좋아요");
        reviews.add("데이트하기 좋은 곳인것같아요 애인이랑 같이 왔어요");


        List<String> keywordList = generator.generateKeyword(reviews);
        for (String keyword:keywordList) {
            System.out.println(keyword);
        }
        //좋다, 같다, 맛있다, 예쁘다, 귀엽다
    }
}

package com.twogather.twogatherwebbackend;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeywordGenerator {

    public List<String> generateKeyword(List<String> reviews) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        List<KomoranResult> komoranResultList = komoran.analyze(reviews,1);
        Map<String, Integer> adjectiveCounts = new HashMap<>();

        for(int i=0;i<komoranResultList.size();i++){
            List<String> strList = komoranResultList.get(i).getMorphesByTags("VA", "NV");
            for (String str:strList){
                if(adjectiveCounts.containsKey(str)){
                    adjectiveCounts.put(str, adjectiveCounts.get(str) + 1);
                }else{
                    adjectiveCounts.put(str, 1);

                }
            }
        }

        // 출현 빈도를 기준으로 정렬
        List<Map.Entry<String, Integer>> sortedAdjectives = new ArrayList<>(adjectiveCounts.entrySet());
        sortedAdjectives.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // 가장 많이 언급된 형용사 출력
        List<String> keywordList = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedAdjectives.size()); i++) {
            Map.Entry<String, Integer> entry = sortedAdjectives.get(i);
            keywordList.add(entry.getKey() + "다");
        }
        return keywordList;

    }
}
package com.twogather.twogatherwebbackend.dto.store;

import com.querydsl.core.Tuple;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoreResponseWithKeyword {
    private Long storeId;
    private String storeName;
    private String address;
    private Double avgScore;
    private List<String> keywordList = new ArrayList<>();
    private String storeImageUrl;
    private long likeCount;

    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score,
                                    List<String> keywordList, String storeImageUrl,
                                    long likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.storeImageUrl = storeImageUrl;
        this.keywordList = keywordList;
        this.avgScore = score;
        this.likeCount = likeCount;

    }
    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score,
                                    String keyword, String storeImageUrl,
                                    long likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.storeImageUrl = storeImageUrl;
        this.keywordList=new ArrayList<>();
        this.keywordList.add(keyword);
        this.avgScore = score;
        this.likeCount = likeCount;

    }
    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score,
                                    List<String> keyword, List<String> storeImageUrl,
                                    long likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.storeImageUrl = storeImageUrl.get(0);
        this.keywordList = keyword;
        this.avgScore = score;
        this.likeCount = likeCount;

    }

    public StoreResponseWithKeyword(NumberPath<Long> storeId, StringPath name, StringPath address, SimpleExpression<Tuple> list, SimpleExpression<Tuple> list1, NumberExpression<Double> avg, Expression<Long> count) {

    }
}

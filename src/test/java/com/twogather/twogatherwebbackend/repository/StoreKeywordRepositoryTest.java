package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StoreKeywordRepositoryTest extends RepositoryTest{
    private static final String KEYWORD_NAME = "맛있는";

    @BeforeEach
    void init(){
        createStore();
        createKeyword();
    }
    @Test
    public void findByStoreId(){
        List<StoreKeyword> list =  storeKeywordRepository.findByStoreId(store.getStoreId());
    }
    @Test
    public void findKeywordsByStoreId(){
        List<Keyword> list = storeKeywordRepository.findKeywordsByStoreId(store.getStoreId());
    }

    private void createKeyword(){
        keywordRepository.save(new Keyword(KEYWORD_NAME));
    }
}

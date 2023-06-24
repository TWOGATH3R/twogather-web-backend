package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.Store;
import org.junit.jupiter.api.Test;

public class KeywordRepositoryTest extends RepositoryTest{
    private static final String KEYWORD_NAME = "맛있는";

    @Test
    public void findByName(){
        createKeyword();
        Keyword keyword = keywordRepository.findByName(KEYWORD_NAME).get();
    }
    private void createKeyword(){
        keywordRepository.save(new Keyword(KEYWORD_NAME));
    }
}

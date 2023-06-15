package com.twogather.twogatherwebbackend.service;


import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.dto.keyword.KeywordResponse;
import com.twogather.twogatherwebbackend.exception.KeywordException;
import com.twogather.twogatherwebbackend.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.K;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.exception.KeywordException.KeywordErrorCode.DUPLICATE_KEYWORD;
import static com.twogather.twogatherwebbackend.exception.KeywordException.KeywordErrorCode.NO_SUCH_KEYWORD;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public List<KeywordResponse> getNKeywordList(int count){
        List<Keyword> keywordList = keywordRepository.findRandomKeywords(count);
        List<KeywordResponse> response = new ArrayList<>();
        for(Keyword keyword: keywordList){
            response.add(new KeywordResponse(keyword.getKeywordId(), keyword.getName()));
        }
        return response;
    }
    public List<String> getKeywordNameList(List<Long> idList){
        return keywordRepository.findAllById(idList)
                .stream()
                .map(Keyword::getName)
                .collect(Collectors.toList());
    }

    private KeywordResponse toResponse(Keyword keyword){
        return new KeywordResponse(keyword.getKeywordId(), keyword.getName());
    }
}

package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import com.twogather.twogatherwebbackend.exception.KeywordException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.KeywordRepository;
import com.twogather.twogatherwebbackend.repository.StoreKeywordRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.K;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.twogather.twogatherwebbackend.exception.KeywordException.KeywordErrorCode.MAXIMUM_KEYWORD_LIMIT;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreKeywordService {
    private final StoreKeywordRepository storeKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final StoreRepository storeRepository;

    public void setStoreKeyword(Long storeId, List<String> keywordStringList){
        if(keywordStringList.size()>3){
            throw new KeywordException(MAXIMUM_KEYWORD_LIMIT);
        }
        for (String keywordString: keywordStringList){
            if(!keywordString.isEmpty()){
                Optional<Keyword> keywordOptional = keywordRepository.findByName(keywordString);
                Keyword keyword;
                if(keywordOptional.isPresent()){
                    keyword = keywordOptional.get();
                }else{
                    keyword = keywordRepository.save(new Keyword(keywordString));
                }

                Store store = storeRepository.findById(storeId).orElseThrow(
                        ()->new StoreException(NO_SUCH_STORE)
                );
                storeKeywordRepository.save(new StoreKeyword(store, keyword));
            }
        }
    }
}

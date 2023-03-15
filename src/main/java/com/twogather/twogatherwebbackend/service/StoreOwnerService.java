package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;

    public void save(StoreOwnerSaveRequest request){
        //사업자 등록번호에 대한 유효성 검사를 실행한다
    }

    private boolean isValid(String businessNumber){
        //api호출
        return true;
    }
}

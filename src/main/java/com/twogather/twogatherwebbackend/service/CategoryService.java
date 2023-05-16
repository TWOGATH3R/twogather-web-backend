package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    public List<CategoryResponse> getCategoryInfos(){
        //TODO: 구현
        return null;
    }
}

package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.exception.CategoryException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.CategoryException.CategoryErrorCode.NO_SUCH_CATEGORY;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    public List<CategoryResponse> getCategoryInfos(){
        List<Category> categoryList = categoryRepository.findAll();
        ArrayList<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (Category category: categoryList){
            categoryResponseList.add(CategoryResponse.from(category.getCategoryId(), category.getName()));
        }
        return categoryResponseList;
    }
    public void setCategoriesForStore(Long storeId, Long categoryId){
        Store store = storeRepository.findActiveStoreById(storeId).orElseThrow(()->  new StoreException(NO_SUCH_STORE));
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new CategoryException(NO_SUCH_CATEGORY));
        store.setCategory(category);
    }
}

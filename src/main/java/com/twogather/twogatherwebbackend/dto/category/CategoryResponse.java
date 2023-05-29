package com.twogather.twogatherwebbackend.dto.category;

import com.twogather.twogatherwebbackend.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long categoryId;
    private String name;

    public static CategoryResponse from(Long categoryId, String name){
        return new CategoryResponse(categoryId, name);
    }
}

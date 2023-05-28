package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.service.CategoryService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final StoreService storeService;

    @GetMapping("/categories")
    public ResponseEntity<Response> getAllCategories() {
        List<CategoryResponse> data = categoryService.getCategoryInfos();

        return ResponseEntity.ok(new Response(data));
    }

    @PatchMapping("/stores/{storeId}/categories/{categoryId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> setCategoriesForStore(@PathVariable Long storeId,
                                                             @PathVariable Long categoryId) {
        categoryService.setCategoriesForStore(storeId, categoryId);
        return ResponseEntity.ok().build();
    }
}

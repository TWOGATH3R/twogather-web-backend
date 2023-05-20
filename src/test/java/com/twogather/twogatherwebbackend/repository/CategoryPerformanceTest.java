package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.dto.category.CategoryRequest;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CategoryPerformanceTest {
    @Autowired
    private CategoryRepository repository;

    @Test
    @Transactional
    public void saveAll(){
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Category category = new Category("Category " + i);
            categories.add(category);
        }
        repository.saveAll(categories);
    }
    @Test
    @Transactional
    public void save(){
        for (int i = 0; i < 100; i++) {
            Category category = new Category("Category " + i);
            repository.save(category);
        }

    }
}

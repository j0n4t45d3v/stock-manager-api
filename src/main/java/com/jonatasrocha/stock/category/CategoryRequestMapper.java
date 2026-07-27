package com.jonatasrocha.stock.category;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.category.CategoryController.CategoryRequest;
import com.jonatasrocha.stock.infra.http.Mapper;

@Component
public class CategoryRequestMapper implements Mapper<CategoryController.CategoryRequest, CategoryEntity> {

    @Override
    public CategoryEntity map(CategoryRequest input) {
        return CategoryEntity.of(input.name());
    }

}

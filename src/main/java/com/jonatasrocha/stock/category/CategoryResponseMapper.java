package com.jonatasrocha.stock.category;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.category.CategoryController.CategoryResponse;
import com.jonatasrocha.stock.infra.http.Mapper;

@Component
public class CategoryResponseMapper implements Mapper<CategoryEntity, CategoryResponse> {

    @Override
    public CategoryResponse map(CategoryEntity input) {
        return new CategoryResponse(input.getId(), input.getName());
    }

}

package com.jonatasrocha.stock.category;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Result<CategoryEntity, ErrorCode> create(CategoryEntity category) {
        if (this.categoryRepository.existsByName(category.getName())) {
            return Result.failure(CategoryErrorCode.CATEGORY_CONFLICT);
        }
        var categorySaved = this.categoryRepository.save(category);
        return Result.success(categorySaved);

    }

    @Transactional
    public Result<Void, ErrorCode> edit(Long id, CategoryEntity category) {
        System.out.println("Teste metodo passou aki");
        if (!this.categoryRepository.existsById(id)) {
            return Result.failure(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        var newCategory = CategoryEntity.of(id, category.getName());
        if (this.categoryRepository.existsByNameAndIdNot(newCategory.getName(), newCategory.getId())) {
            return Result.failure(CategoryErrorCode.CATEGORY_CONFLICT);
        }

        this.categoryRepository.save(newCategory);
        return Result.successVoid();
    }

    @Transactional
    public Result<Void, ErrorCode> removeById(Long id) {
        if (!this.categoryRepository.existsById(id)) {
            return Result.failure(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        this.categoryRepository.deleteById(id);
        return Result.successVoid();
    }

    public Optional<CategoryEntity> findCategoryById(Long id) {
       return this.categoryRepository.findById(id);
    }


}

package com.jonatasrocha.stock.category;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping("/v1/categories")
public class CategoryController extends BaseController {

    private static final String CATEGORY_NOT_FOUND_CODE = "CATEGORY_NOT_FOUND";
    private static final String CATEGORY_CONFLICT_CODE = "CATEGORY_CONFLICT";

    private static final String CATEGORY_NOT_FOUND_DEFAULT_MESSAGE = "Category not found";
    private static final String CATEGORY_NAME_ALREADY_USED = "This category name is already used by other category";

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public record CategoryRequest(
        @NotBlank
        String name
    ) {}

    public record CategoryResponse(Long id, String name) {

        public static CategoryResponse ofEntity(CategoryEntity entity) {
            return new CategoryResponse(entity.getId(), entity.getName());
        }

    }

    @Transactional
    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid CategoryRequest request) {
        var newCategory = CategoryEntity.of(request.name());
        if (this.categoryRepository.existsByName(newCategory.getName())) {
            return responseConflict(CATEGORY_CONFLICT_CODE,CATEGORY_NAME_ALREADY_USED);
        }
        var categorySaved = this.categoryRepository.save(newCategory);
        return responseCreated(
            CategoryResponse.ofEntity(categorySaved),
            "/v1/categories/{id}",
            categorySaved.getId()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var categoryFound = this.categoryRepository.findById(id);
        if (categoryFound.isEmpty()) {
            return responseNotFound(CATEGORY_NOT_FOUND_CODE, CATEGORY_NOT_FOUND_DEFAULT_MESSAGE);
        }
        return responseOk(CategoryResponse.ofEntity(categoryFound.get()));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Response> update(
        @PathVariable("id") Long id,
        @RequestBody @Valid CategoryRequest request
    ) {
        var categoryFound = this.categoryRepository.findById(id);
        if (categoryFound.isEmpty()) {
            return responseNotFound(CATEGORY_NOT_FOUND_CODE, CATEGORY_NOT_FOUND_DEFAULT_MESSAGE);
        }

        var category = categoryFound.get();
        var newCategory = CategoryEntity.of(category.getId(), request.name());
        if (this.categoryRepository.existsByNameAndIdNot(newCategory.getName(), newCategory.getId())) {
            return responseConflict(CATEGORY_CONFLICT_CODE, CATEGORY_NAME_ALREADY_USED);
        }

        this.categoryRepository.save(newCategory);
        return responseNoContent();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> remove(@PathVariable("id") Long id) {
        if (!this.categoryRepository.existsById(id)) {
            return responseNotFound(CATEGORY_NOT_FOUND_CODE, CATEGORY_NOT_FOUND_DEFAULT_MESSAGE);
        }

        this.categoryRepository.deleteById(id);
        return responseNoContent();
    }

}

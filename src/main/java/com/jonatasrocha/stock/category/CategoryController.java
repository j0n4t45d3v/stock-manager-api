package com.jonatasrocha.stock.category;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.jonatasrocha.stock.infra.http.Response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

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
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.ofFailure(
                    "CATEGORY_CONFLICT", 
                    "Already exists a category with this same name"
                ));
        }
        var categorySaved = this.categoryRepository.save(newCategory);
        var location = UriComponentsBuilder
                        .fromPath("/{id}")
                        .buildAndExpand(categorySaved.getId())
                        .toUri();

        var categoryResponse = CategoryResponse.ofEntity(categorySaved);
        return ResponseEntity
            .created(location)
            .body(Response.ofSuccess(categoryResponse));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var categoryFound = this.categoryRepository.findById(id);
        if (categoryFound.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.ofFailure(
                    "CATEGORY_NOT_FOUND", 
                    "Category not found"
                ));
        }
        var categoryResponse = CategoryResponse.ofEntity(categoryFound.get());
        return ResponseEntity.ok(Response.ofSuccess(categoryResponse));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid CategoryRequest request) {
        var categoryFound = this.categoryRepository.findById(id);
        if (categoryFound.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.ofFailure(
                    "CATEGORY_NOT_FOUND",
                    "Category not found"
                ));
        }

        var category = categoryFound.get();
        var newCategory = CategoryEntity.of(category.getId(), request.name());
        if (this.categoryRepository.existsByNameAndIdNot(newCategory.getName(), newCategory.getId())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.ofFailure(
                    "CATEGORY_CONFLICT",
                     "Already exists a category with this same name"
                ));
        }

        this.categoryRepository.save(newCategory);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Long id) {
        if (!this.categoryRepository.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.ofFailure(
                    "CATEGORY_NOT_FOUND",
                    "Category not found"
                ));
        }

        this.categoryRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}

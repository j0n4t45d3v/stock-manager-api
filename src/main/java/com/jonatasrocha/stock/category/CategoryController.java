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

    private final CategoryService categoryService;

    private final CategoryResponseMapper categoryResponseMapper;

    private final CategoryRequestMapper categoryRequestMapper;

    public CategoryController(
        CategoryService categoryService,
        CategoryRequestMapper categoryRequestMapper,
        CategoryResponseMapper categoryResponseMapper
    ) {
        this.categoryService = categoryService;
        this.categoryRequestMapper = categoryRequestMapper;
        this.categoryResponseMapper = categoryResponseMapper;
    }

    public record CategoryRequest(@NotBlank String name) {}

    public record CategoryResponse(Long id, String name) {}

    @Transactional
    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid CategoryRequest request) {
        var createResult = this.categoryService.create(this.categoryRequestMapper.map(request));
        if (createResult.isFailure()) {
            return responseFail(createResult.error());
        }
        var entity = createResult.data();
        return responseCreated(
            this.categoryResponseMapper.map(entity),
            "/v1/categories/{id}",
            entity.getId()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var categoryFound = this.categoryService.findCategoryById(id);
        if (categoryFound.isEmpty()) {
            return responseFail(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
        return responseOk(this.categoryResponseMapper.map(categoryFound.get()));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Response> update(
        @PathVariable("id") Long id,
        @RequestBody @Valid CategoryRequest request
    ) {
        var editResult = this.categoryService.edit(id, this.categoryRequestMapper.map(request));
        if (editResult.isFailure()) {
            return responseFail(editResult.error());
        }
        return responseNoContent();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> remove(@PathVariable("id") Long id) {
        var removeResult = this.categoryService.removeById(id);
        if (removeResult.isFailure()) {
            return responseFail(removeResult.error());
        }
        return responseNoContent();
    }

}

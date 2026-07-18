package com.jonatasrocha.stock.product;

import java.math.BigDecimal;

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

import com.jonatasrocha.stock.category.CategoryRepository;
import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;
import com.jonatasrocha.stock.supplier.SupplierRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@RestController
@RequestMapping("/v1/products")
public class ProductController extends BaseController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductController(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        SupplierRepository supplierRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    public record ProductRequest(
        @NotBlank
        @Size(max = 40)
        String name,

        @NotBlank
        @Size(max = 255)
        String description,

        @NotBlank
        @Size(max = 50)
        String sku,

        @NotNull
        @DecimalMin(value = "0.01")
        @DecimalMax(value = "9999999.999")
        BigDecimal unitPrice,

        @NotNull
        Long supplierId,

        @NotNull
        Long categoryId
    ) {}

    public record ProductResponse(
        Long id, 
        String name,
        String description,
        String sku,
        BigDecimal unitPrice,
        Long supplierId,
        Long categoryId
    ) {
        public static ProductResponse ofEntity(ProductEntity entity) {
            return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSkuValue(),
                entity.getUnitPriceValue(),
                entity.getSupplierId(),
                entity.getCategoryId()
            );
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid ProductRequest request) {

        var categoryFound = this.categoryRepository.findById(request.categoryId());
        if (categoryFound.isEmpty()) {
            return responseNotFound("CATEGORY_NOT_FOUND", "Category not found");
        }

        var supplierFound = this.supplierRepository.findById(request.supplierId());
        if (supplierFound.isEmpty()) {
            return responseNotFound("SUPPLIER_NOT_FOUND", "Supplier not found");
        }

        var newProduct = ProductEntity
            .builder()
            .name(request.name())
            .description(request.description())
            .sku(request.sku())
            .unitPrice(request.unitPrice())
            .supplier(supplierFound.get())
            .category(categoryFound.get())
            .build();

        if (this.productRepository.existsBySku(newProduct.getSku())) {
            return responseConflict("PRODUCT_SKU_CONFLICT", "Already exists a product with this same sku");
        }
        var productSaved = this.productRepository.save(newProduct);
        var productId = productSaved.getId();
        var content = ProductResponse.ofEntity(productSaved);
        return responseCreated(content, "/v1/products/{id}", productId);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var productFound = this.productRepository.findById(id);
        if (productFound.isEmpty()) {
            return responseNotFound("PRODUCT_NOT_FOUND", "Product not found");
        }
        var productResponse = ProductResponse.ofEntity(productFound.get());
        return responseOk(productResponse);
    }


    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Response> update(
        @PathVariable("id") Long id,
        @RequestBody @Valid ProductRequest request
    ) {
        var productFound = this.productRepository.findById(id);
        if (productFound.isEmpty()) {
            return responseNotFound("PRODUCT_NOT_FOUND", "Product not found");
        }

        var product = productFound.get();
        var categoryFound = this.categoryRepository.findById(request.categoryId());
        if (categoryFound.isEmpty()) {
            return responseNotFound("CATEGORY_NOT_FOUND", "Category not found");
        }

        var supplierFound = this.supplierRepository.findById(request.supplierId());
        if (supplierFound.isEmpty()) {
            return responseNotFound("SUPPLIER_NOT_FOUND", "Supplier not found");
        }

        var productEdited = ProductEntity.builder(product.getId())
            .name(request.name())
            .description(request.description())
            .sku(request.sku())
            .unitPrice(request.unitPrice())
            .supplier(supplierFound.get())
            .category(categoryFound.get())
            .build();
        ;
        if (this.productRepository.existsBySkuAndIdNot(productEdited.getSku(), productEdited.getId())) {
            return responseConflict("PRODUCT_SKU_CONFLICT", "Already exists a product with same sku");
        }

        this.productRepository.save(productEdited);
        return responseNoContent();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Long id) {
        if (!this.productRepository.existsById(id)) {
            return responseNotFound("CATEGORY_NOT_FOUND", "Category not found");
        }
        this.productRepository.deleteById(id);
        return responseNoContent();
    }

}

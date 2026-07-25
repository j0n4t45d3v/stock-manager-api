package com.jonatasrocha.stock.product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jonatasrocha.stock.category.CategoryRepository;
import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;
import com.jonatasrocha.stock.product.ProductEntity.UnitPrice;
import com.jonatasrocha.stock.product.ProductMovementEntity.Note;
import com.jonatasrocha.stock.product.ProductMovementEntity.Quantity;
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
    private final StockStateRepository stockStateRepository;
    private final ProductMovementRepository productMovementRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductController(
        ProductRepository productRepository,
        StockStateRepository stockStateRepository,
        CategoryRepository categoryRepository,
        SupplierRepository supplierRepository,
        ProductMovementRepository productMovementRepository
    ) {
        this.productRepository = productRepository;
        this.stockStateRepository = stockStateRepository;
        this.productMovementRepository = productMovementRepository;
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
        this.stockStateRepository.save(StockStateEntity.of(productSaved));
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

    public record ProductMovementRequest(
        @NotNull
        ProductMovementEntity.Type type,

        @NotNull
        @DecimalMin(value = "0.01")
        @DecimalMax(value = "9999999.999")
        BigDecimal quantity,

        @NotBlank
        String note,
        
        @NotNull
        Long productId,

        BigDecimal unitPrice
    ) {
    }

    public record ProductMovementResponse(
        String type,
        BigDecimal quantity,
        String note,
        Long productId,
        BigDecimal unitPrice
    ) {
        public static ProductMovementResponse ofEntity(ProductMovementEntity entity) {
            return new ProductMovementResponse(
                entity.getTypeName(),
                entity.getQuantityValue(),
                entity.getNoteValue(),
                entity.getProductId(),
                entity.getUnitPriceValue()
            );
        }
    }

    @Transactional
    @PostMapping("/movements")
    public ResponseEntity<Response> createMovement(@RequestBody @Valid ProductMovementRequest request) {
        var product = this.productRepository.findById(request.productId());
        if (product.isEmpty()) {
            return responseNotFound("PRODUCT_NOT_FOUND", "Product not found");
        }

        if (request.type().isDecrease()) {
            var rowsAffected = this.stockStateRepository.decreaseBalance(request.quantity(), product.get());
            if (rowsAffected <= 0) {
                return responseFail(HttpStatus.BAD_REQUEST, "INSUFICCIENT_STOCK", "Product not has " + request.quantity() + " available in stock");
            }
        } else {
            this.stockStateRepository.incrementBalance(request.quantity(), product.get());
        }

        var movement = ProductMovementEntity.ofType(
            request.type(),
            new Quantity(request.quantity()),
            new Note(request.note()),
            product.get(),
            request.unitPrice() == null ? null :  new UnitPrice(request.unitPrice())
        );

        var movementSaved = this.productMovementRepository.save(movement);

        return responseCreated(
            ProductMovementResponse.ofEntity(movementSaved),
            "/v1/products/movements/{id}",
            movementSaved.getId()
        );
    }

    @GetMapping("/{productId}/movements")
    public ResponseEntity<Response> getProductMovements(
        @PathVariable("productId") Long productId,
        @RequestParam(name = "limit", defaultValue = "50", required = false) Integer limit,
        @RequestParam(name = "offset", required = false) Long offset
    ) {
        var movementPage = this.productMovementRepository
            .findAllMovementByProduct(productId, offset, PageRequest.ofSize(limit));
        return responseOk(CursorPageResponse.ofPage(movementPage));
    }

    public record CursorPageResponse(
        List<ProductMovementResponse> content,
        Long nextOffset
    ) {

        public static CursorPageResponse ofPage(Page<ProductMovementEntity> page) {
            var lessId = page.get()
                .map(p -> p.getId())
                .min((l, r) -> l.compareTo(r));
            Long nextOffset = null;
            if (lessId.isPresent()) {
                nextOffset = lessId.get();
            }
            var content = page.map(ProductMovementResponse::ofEntity).getContent();
            return new CursorPageResponse(content, nextOffset);
        }

    }

}

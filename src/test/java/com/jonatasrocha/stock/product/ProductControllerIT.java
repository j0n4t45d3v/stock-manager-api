package com.jonatasrocha.stock.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.jonatasrocha.stock.BaseIntegrationTest;
import com.jonatasrocha.stock.category.CategoryEntity;
import com.jonatasrocha.stock.category.CategoryRepository;
import com.jonatasrocha.stock.supplier.SupplierEntity;
import com.jonatasrocha.stock.supplier.SupplierRepository;

@Transactional
class ProductControllerIT extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private static final String PRODUCT_ENDPOINT = "/v1/products";

    private static final String JSON_PATH_ID = "$.data.id";
    private static final String JSON_PATH_NAME = "$.data.name";
    private static final String JSON_PATH_SKU = "$.data.sku";
    private static final String JSON_PATH_ERROR_CODE = "$.error.code";

    private static final String ERROR_PRODUCT_CONFLICT = "PRODUCT_SKU_CONFLICT";
    private static final String ERROR_PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    private static final String ERROR_CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
    private static final String ERROR_SUPPLIER_NOT_FOUND = "SUPPLIER_NOT_FOUND";

    private CategoryEntity createCategory(String name) {
        return this.categoryRepository.save(CategoryEntity.of(name));
    }

    private SupplierEntity createSupplier(String name, String email, String phone) {
        return this.supplierRepository.save(SupplierEntity.of(name, email, phone));
    }

    private ProductEntity createProduct(String name, String sku, BigDecimal unitPrice, CategoryEntity category, SupplierEntity supplier) {
        return this.productRepository.save(
                ProductEntity.builder()
                        .name(name)
                        .description("A product")
                        .sku(sku)
                        .unitPrice(unitPrice)
                        .supplier(supplier)
                        .category(category)
                        .build()
        );
    }

    @Nested
    class ApiV1CreateProduct {

        @Test
        void shouldReturnCreatedWhenBodyIsValid() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeCreateRequest("Coffee", "Coffee description", "SKU-001", new BigDecimal("19.90"), supplier.getId(), category.getId()))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath(JSON_PATH_ID).isNumber())
                    .andExpect(jsonPath(JSON_PATH_NAME).value("Coffee"))
                    .andExpect(jsonPath(JSON_PATH_SKU).value("SKU-001"));

            assertEquals(1, productRepository.count());
        }

        @Test
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeCreateRequest("Coffee", "Coffee description", "SKU-001", new BigDecimal("19.90"), supplier.getId(), 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_NOT_FOUND));
        }

        @Test
        void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
            var category = createCategory("Food");

            mockMvc.perform(makeCreateRequest("Coffee", "Coffee description", "SKU-001", new BigDecimal("19.90"), 999L, category.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_NOT_FOUND));
        }

        @Test
        void shouldReturnConflictWhenSkuAlreadyExists() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);

            mockMvc.perform(makeCreateRequest("Coffee", "Coffee description", "SKU-001", new BigDecimal("19.90"), supplier.getId(), category.getId()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_PRODUCT_CONFLICT));

            assertEquals(1, productRepository.count());
        }

        @Test
        void shouldReturnUnprocessableContentWhenNameIsBlank() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeCreateRequest("", "Coffee description", "SKU-001", new BigDecimal("19.90"), supplier.getId(), category.getId()))
                    .andExpect(status().isUnprocessableContent());
        }

        @Test
        void shouldReturnUnprocessableContentWhenPriceIsBelowMinimum() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeCreateRequest("Coffee", "Coffee description", "SKU-001", new BigDecimal("0.00"), supplier.getId(), category.getId()))
                    .andExpect(status().isUnprocessableContent());
        }
    }

    @Nested
    class ApiV1GetProduct {

        @Test
        void shouldReturnOkWhenProductExists() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);

            mockMvc.perform(get(PRODUCT_ENDPOINT + "/" + product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(JSON_PATH_ID).value(product.getId()))
                    .andExpect(jsonPath(JSON_PATH_NAME).value(product.getName()))
                    .andExpect(jsonPath(JSON_PATH_SKU).value(product.getSkuValue()));
        }

        @Test
        void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
            mockMvc.perform(get(PRODUCT_ENDPOINT + "/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_PRODUCT_NOT_FOUND));
        }
    }

    @Nested
    class ApiV1UpdateProduct {

        @Test
        void shouldReturnNoContentWhenBodyIsValid() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);

            mockMvc.perform(makeUpdateRequest(product.getId(), "Tea", "Tea description", "SKU-002", new BigDecimal("12.50"), supplier.getId(), category.getId()))
                    .andExpect(status().isNoContent());

            var updated = productRepository.findById(product.getId()).orElseThrow();

            assertEquals("Tea", updated.getName());
            assertEquals("SKU-002", updated.getSkuValue());
            assertEquals(new BigDecimal("12.50"), updated.getUnitPriceValue());
        }

        @Test
        void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeUpdateRequest(999L, "Tea", "Tea description", "SKU-002", new BigDecimal("12.50"), supplier.getId(), category.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_PRODUCT_NOT_FOUND));
        }

        @Test
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), createCategory("Food"), supplier);

            mockMvc.perform(makeUpdateRequest(product.getId(), "Tea", "Tea description", "SKU-002", new BigDecimal("12.50"), supplier.getId(), 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_NOT_FOUND));
        }

        @Test
        void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
            var category = createCategory("Food");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, createSupplier("ACME", "supplier@example.com", "+5511999999999"));

            mockMvc.perform(makeUpdateRequest(product.getId(), "Tea", "Tea description", "SKU-002", new BigDecimal("12.50"), 999L, category.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_NOT_FOUND));
        }

        @Test
        void shouldReturnConflictWhenSkuAlreadyExists() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);
            var productToUpdate = createProduct("Tea", "SKU-002", new BigDecimal("12.50"), category, supplier);

            mockMvc.perform(makeUpdateRequest(productToUpdate.getId(), "Tea", "Tea description", "SKU-001", new BigDecimal("12.50"), supplier.getId(), category.getId()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_PRODUCT_CONFLICT));
        }

        @Test
        void shouldReturnUnprocessableContentWhenNameIsBlank() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);

            mockMvc.perform(makeUpdateRequest(product.getId(), "", "Tea description", "SKU-002", new BigDecimal("12.50"), supplier.getId(), category.getId()))
                    .andExpect(status().isUnprocessableContent());
        }
    }

    @Nested
    class ApiV1DeleteProduct {

        @Test
        void shouldReturnNoContentWhenProductExists() throws Exception {
            var category = createCategory("Food");
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var product = createProduct("Coffee", "SKU-001", new BigDecimal("19.90"), category, supplier);

            mockMvc.perform(delete(PRODUCT_ENDPOINT + "/" + product.getId()))
                    .andExpect(status().isNoContent());

            assertFalse(productRepository.existsById(product.getId()));
        }

        @Test
        void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
            mockMvc.perform(delete(PRODUCT_ENDPOINT + "/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_PRODUCT_NOT_FOUND));
        }
    }

    private RequestBuilder makeCreateRequest(String name, String description, String sku, BigDecimal unitPrice, Long supplierId, Long categoryId) {
        return post(PRODUCT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload(name, description, sku, unitPrice, supplierId, categoryId));
    }

    private RequestBuilder makeUpdateRequest(Long id, String name, String description, String sku, BigDecimal unitPrice, Long supplierId, Long categoryId) {
        return put(PRODUCT_ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload(name, description, sku, unitPrice, supplierId, categoryId));
    }

    private String productPayload(String name, String description, String sku, BigDecimal unitPrice, Long supplierId, Long categoryId) {
        return """
                {
                    "name": "%s",
                    "description": "%s",
                    "sku": "%s",
                    "unitPrice": %s,
                    "supplierId": %s,
                    "categoryId": %s
                }
                """.formatted(name, description, sku, unitPrice, supplierId, categoryId);
    }
}

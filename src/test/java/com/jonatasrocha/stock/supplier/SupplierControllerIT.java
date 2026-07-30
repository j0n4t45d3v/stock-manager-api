package com.jonatasrocha.stock.supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.jonatasrocha.stock.BaseIntegrationTest;

@Transactional
class SupplierControllerIT extends BaseIntegrationTest {

    @Autowired
    private SupplierRepository supplierRepository;

    private static final String SUPPLIER_ENDPOINT = "/v1/suppliers";

    private static final String JSON_PATH_ID = "$.data.id";
    private static final String JSON_PATH_NAME = "$.data.name";
    private static final String JSON_PATH_EMAIL = "$.data.email";
    private static final String JSON_PATH_PHONE = "$.data.phone";
    private static final String JSON_PATH_ERROR_CODE = "$.error.code";

    private static final String ERROR_SUPPLIER_CONFLICT = "SUPPLIER_CONFLICT";
    private static final String ERROR_SUPPLIER_NOT_FOUND = "SUPPLIER_NOT_FOUND";

    private SupplierEntity createSupplier(String name, String email, String phone) {
        return this.supplierRepository.save(SupplierEntity.of(name, email, phone));
    }

    @Nested
    class ApiV1CreateSupplier {

        @Test
        void shouldReturnCreatedWhenBodyIsValid() throws Exception {
            mockMvc.perform(makeCreateRequest("ACME", "supplier@example.com", "+5511999999999"))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath(JSON_PATH_ID).isNumber())
                    .andExpect(jsonPath(JSON_PATH_NAME).value("ACME"))
                    .andExpect(jsonPath(JSON_PATH_EMAIL).value("supplier@example.com"))
                    .andExpect(jsonPath(JSON_PATH_PHONE).value("+5511999999999"));

            assertEquals(1, supplierRepository.count());
        }

        @Test
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
            createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeCreateRequest("ACME", "supplier@example.com", "+5511999999999"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_CONFLICT));

            assertEquals(1, supplierRepository.count());
        }

        @Test
        void shouldReturnUnprocessableContentWhenNameIsBlank() throws Exception {
            mockMvc.perform(makeCreateRequest("", "supplier@example.com", "+5511999999999"))
                    .andExpect(status().isUnprocessableContent());
        }

        @Test
        void shouldReturnUnprocessableContentWhenEmailIsInvalid() throws Exception {
            mockMvc.perform(makeCreateRequest("ACME", "invalid-email", "+5511999999999"))
                    .andExpect(status().isUnprocessableContent());
        }

        @Test
        void shouldReturnUnprocessableContentWhenPhoneIsInvalid() throws Exception {
            mockMvc.perform(makeCreateRequest("ACME", "supplier@example.com", "9999"))
                    .andExpect(status().isUnprocessableContent());
        }
    }

    @Nested
    class ApiV1GetSupplier {

        @Test
        void shouldReturnOkWhenSupplierExists() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(get(SUPPLIER_ENDPOINT + "/" + supplier.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(JSON_PATH_ID).value(supplier.getId()))
                    .andExpect(jsonPath(JSON_PATH_NAME).value(supplier.getName()))
                    .andExpect(jsonPath(JSON_PATH_EMAIL).value(supplier.getEmailValue()))
                    .andExpect(jsonPath(JSON_PATH_PHONE).value(supplier.getPhoneValue()));
        }

        @Test
        void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
            mockMvc.perform(get(SUPPLIER_ENDPOINT + "/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_NOT_FOUND));
        }
    }

    @Nested
    class ApiV1UpdateSupplier {

        @Test
        void shouldReturnNoContentWhenBodyIsValid() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeUpdateRequest(supplier.getId(), "Market", "market@example.com", "+5511888888888"))
                    .andExpect(status().isNoContent());

            var updated = supplierRepository.findById(supplier.getId()).orElseThrow();

            assertEquals("Market", updated.getName());
            assertEquals("market@example.com", updated.getEmailValue());
            assertEquals("+5511888888888", updated.getPhoneValue());
        }

        @Test
        void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
            mockMvc.perform(makeUpdateRequest(999L, "Market", "market@example.com", "+5511888888888"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_NOT_FOUND));
        }

        @Test
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
            var firstSupplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");
            var secondSupplier = createSupplier("Market", "market@example.com", "+5511888888888");

            mockMvc.perform(makeUpdateRequest(secondSupplier.getId(), "Market", firstSupplier.getEmailValue(), "+5511888888888"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_CONFLICT));
        }

        @Test
        void shouldReturnUnprocessableContentWhenNameIsBlank() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(makeUpdateRequest(supplier.getId(), "", "supplier@example.com", "+5511999999999"))
                    .andExpect(status().isUnprocessableContent());
        }
    }

    @Nested
    class ApiV1DeleteSupplier {

        @Test
        void shouldReturnNoContentWhenSupplierExists() throws Exception {
            var supplier = createSupplier("ACME", "supplier@example.com", "+5511999999999");

            mockMvc.perform(delete(SUPPLIER_ENDPOINT + "/" + supplier.getId()))
                    .andExpect(status().isNoContent());

            assertFalse(supplierRepository.existsById(supplier.getId()));
        }

        @Test
        void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
            mockMvc.perform(delete(SUPPLIER_ENDPOINT + "/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_SUPPLIER_NOT_FOUND));
        }
    }

    private RequestBuilder makeCreateRequest(String name, String email, String phone) {
        return post(SUPPLIER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierPayload(name, email, phone));
    }

    private RequestBuilder makeUpdateRequest(Long id, String name, String email, String phone) {
        return put(SUPPLIER_ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierPayload(name, email, phone));
    }

    private String supplierPayload(String name, String email, String phone) {
        return """
                {
                    "name": "%s",
                    "email": "%s",
                    "phone": "%s"
                }
                """.formatted(name, email, phone);
    }
}

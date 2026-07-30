package com.jonatasrocha.stock.category;

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
class CategoryControllerIT extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String CATEGORY_ENDPOINT = "/v1/categories";

    private static final String JSON_PATH_ID = "$.data.id";
    private static final String JSON_PATH_NAME = "$.data.name";
    private static final String JSON_PATH_ERROR_CODE = "$.error.code";

    private static final String ERROR_CATEGORY_CONFLICT = "CATEGORY_CONFLICT";
    private static final String ERROR_CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";

    private CategoryEntity createCategory(String name) {
        return this.categoryRepository.save(CategoryEntity.of(name));
    }

    @Nested
    class ApiV1CreateCategory {

        @Test
        void shouldReturnCreatedWhenBodyIsValid() throws Exception {
            mockMvc.perform(makeCreateRequest("Food")).andExpect(status().isCreated())
                    .andExpect(header().exists("Location")).andExpect(jsonPath(JSON_PATH_ID).isNumber())
                    .andExpect(jsonPath(JSON_PATH_NAME).value("Food"));

            assertEquals(1, categoryRepository.count());
        }

        @Test
        void shouldReturnConflictWhenCategoryAlreadyExists() throws Exception {
            createCategory("Food");

            mockMvc.perform(makeCreateRequest("Food")).andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_CONFLICT));

            assertEquals(1, categoryRepository.count());
        }

        @Test
        void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
            mockMvc.perform(makeCreateRequest("")).andExpect(status().isUnprocessableContent());
        }

    }

    @Nested
    class ApiV1GetCategory {

        @Test
        void shouldReturnOkWhenCategoryExists() throws Exception {
            var category = createCategory("Food");

            mockMvc.perform(get(CATEGORY_ENDPOINT + "/" + category.getId())).andExpect(status().isOk())
                    .andExpect(jsonPath(JSON_PATH_ID).value(category.getId()))
                    .andExpect(jsonPath(JSON_PATH_NAME).value(category.getName()));
        }

        @Test
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            mockMvc.perform(get(CATEGORY_ENDPOINT + "/999")).andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_NOT_FOUND));
        }

    }

    @Nested
    class ApiV1UpdateCategory {

        @Test
        void shouldReturnNoContentWhenBodyIsValid() throws Exception {
            var category = createCategory("Food");

            mockMvc.perform(makeUpdateRequest(category.getId(), "Market")).andExpect(status().isNoContent());

            var updated = categoryRepository.findById(category.getId()).orElseThrow();

            assertEquals("Market", updated.getName());
        }

        @Test
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            mockMvc.perform(makeUpdateRequest(999L, "Market")).andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_NOT_FOUND));
        }

        @Test
        void shouldReturnConflictWhenCategoryNameAlreadyExists() throws Exception {
            var food = createCategory("Food");
            var market = createCategory("Market");

            mockMvc.perform(makeUpdateRequest(market.getId(), food.getName())).andExpect(status().isConflict())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_CONFLICT));
        }

        @Test
        void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
            var category = createCategory("Food");

            mockMvc.perform(makeUpdateRequest(category.getId(), "")).andExpect(status().isUnprocessableContent());
        }

    }

    @Nested
    class ApiV1DeleteCategory {

        @Test
        void shouldReturnNoContentWhenCategoryExists() throws Exception {
            var category = createCategory("Food");

            mockMvc.perform(delete(CATEGORY_ENDPOINT + "/" + category.getId())).andExpect(status().isNoContent());

            assertFalse(categoryRepository.existsById(category.getId()));
        }

        @Test
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            mockMvc.perform(delete(CATEGORY_ENDPOINT + "/999")).andExpect(status().isNotFound())
                    .andExpect(jsonPath(JSON_PATH_ERROR_CODE).value(ERROR_CATEGORY_NOT_FOUND));
        }

    }

    private RequestBuilder makeCreateRequest(String name) {

        return post(CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(categoryPayload(name));
    }

    private RequestBuilder makeUpdateRequest(Long id, String name) {

        return put(CATEGORY_ENDPOINT + "/" + id).contentType(MediaType.APPLICATION_JSON).content(categoryPayload(name));
    }

    private String categoryPayload(String name) {

        return """
                {
                    "name": "%s"
                }
                """.formatted(name);
    }

}

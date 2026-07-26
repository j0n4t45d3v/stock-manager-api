package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/inventories")
public class InventoryController extends BaseController {

    private final InventoryService inventoryService;
    private final InventoryRequestMapper requestMapper;
    private final InventoryResponseMapper responseMapper;

    public InventoryController(
        InventoryService inventoryService,
        InventoryRequestMapper requestMapper,
        InventoryResponseMapper responseMapper
    ) {
        this.inventoryService = inventoryService;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    public record InventoryRequest(
        @NotNull
        MovementType type,

        @NotNull
        @DecimalMin(value = "0.01")
        @DecimalMax(value = "9999999.999")
        BigDecimal quantity,

        @NotBlank
        String note,

        @NotNull
        Long productId,

        @NotNull
        BigDecimal unitPrice
    ) {
    }

    public record InventoryResponse(
        String type,
        BigDecimal quantity,
        String note,
        Long productId,
        BigDecimal unitPrice
    ) {
    }

    @PostMapping("/movements")
    public ResponseEntity<Response> makeMovement(@RequestBody InventoryRequest request) {
        var result = this.inventoryService.registerMovement(this.requestMapper.map(request));
        if (result.isFailure()) {
            var error = result.error();
            return responseFail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                error.code(),
                error.message()
            );
        }
        var entity = result.data();
        return responseCreated(
            this.responseMapper.map(entity),
            "/v1/inventories/movements/{id}",
            entity.getId()
        );
    }

    @GetMapping("/{productId}/movements")
    public ResponseEntity<Response> getProductMovements(
        @PathVariable("productId") Long productId,
        @RequestParam(name = "limit", defaultValue = "50", required = false)
        Integer limit,
        @RequestParam(name = "offset", required = false) Long offset
    ) {
        var movementPage = this.inventoryService
            .getMovementsByProduct(productId, offset, limit);
        return responseCursorPage(movementPage, this.responseMapper);
    }

}

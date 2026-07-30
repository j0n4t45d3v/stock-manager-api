package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Component
public class ReleaseReserveQuantityStock implements StockQuantityOperation {

    private final StockStateRepository stockStateRepository;

    public ReleaseReserveQuantityStock(StockStateRepository stockStateRepository) {
        this.stockStateRepository = stockStateRepository;
    }

    @Override
    public MovementType type() {
        return MovementType.RELEASE_RESERVE;
    }

    @Override
    public Result<Void, ErrorCode> apply(BigDecimal amount, Long product) {
        var rowsAffected = this.stockStateRepository.releaseReserveBalance(amount, product);
        if (rowsAffected <= 0) {
            return Result.failure(InventoryErrorCode.NOT_AVAILABLE_QUANTITY_TO_RELEASE);
        }
        return Result.successVoid();
    }
}

package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Component
public class ReserveQuantityStock implements StockQuantityOperation {

    private final StockStateRepository stockStateRepository;

    public ReserveQuantityStock(StockStateRepository stockStateRepository) {
        this.stockStateRepository = stockStateRepository;
    }

    @Override
    public MovementType type() {
        return MovementType.RESERVE;
    }

    @Override
    public Result<Void, ErrorCode> apply(BigDecimal amount, Long product) {
        var rowsAffected = this.stockStateRepository.reserveBalance(amount, product);
        if (rowsAffected <= 0) {
            return Result.failure(InventoryErrorCode.INSUFICCIENT_STOCK);
        }
        return Result.successVoid();
    }
}

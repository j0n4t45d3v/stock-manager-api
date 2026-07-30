package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Component
public class IncreaseQuantityStock implements StockQuantityOperation {

    private final StockStateRepository stockStateRepository;

    public IncreaseQuantityStock(StockStateRepository stockStateRepository) {
        this.stockStateRepository = stockStateRepository;
    }

    @Override
    public MovementType type() {
        return MovementType.ENTRY;
    }

    @Override
    public Result<Void, ErrorCode> apply(BigDecimal amount, Long product) {
        this.stockStateRepository.increaseBalance(amount, product);
        return Result.successVoid();
    }


}
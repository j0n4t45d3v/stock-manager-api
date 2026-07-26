package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

public interface StockQuantityOperation {

    MovementType type();

    Result<Void, ErrorCode> apply(BigDecimal amount, Long product);

}

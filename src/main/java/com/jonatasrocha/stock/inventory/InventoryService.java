package com.jonatasrocha.stock.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Service
public class InventoryService {

    private final StockStateRepository stockStateRepository;
    private final ProductMovementRepository productMovementRepository;
    private final StockQuantityOperationFactory stockQuantityOperationFactory;

    public InventoryService(
        StockStateRepository stockStateRepository,
        ProductMovementRepository productMovementRepository,
        StockQuantityOperationFactory stockQuantityOperationFactory
    ) {
        this.stockStateRepository = stockStateRepository;
        this.productMovementRepository = productMovementRepository;
        this.stockQuantityOperationFactory = stockQuantityOperationFactory;
    }

    @Transactional
    public Result<ProductMovementEntity, ErrorCode> registerMovement(ProductMovementEntity movement) {
        var strategy = this.stockQuantityOperationFactory.get(movement.getType()); 
        var result = strategy.apply(movement.getQuantityValue(), movement.getProductId());
        if (result.isFailure()) {
            return Result.failure(result.error());
        }
        var movementSaved = this.productMovementRepository.save(movement);
        return Result.success(movementSaved);
    }

    public Page<ProductMovementEntity> getMovementsByProduct(
        long productId,
        Long offset,
        int limit
    ) {
        return this.productMovementRepository
            .findAllMovementByProduct(productId, offset, PageRequest.ofSize(limit));
    }

    public Result<Void, ErrorCode> createInventory(Long productId) {
        if(this.stockStateRepository.existsByProductId(productId)) {
            return Result.failure(InventoryErrorCode.INVENTORY_ALREADY_EXISTS);
        }
        var stockState = StockStateEntity.of(productId);
        this.stockStateRepository.save(stockState);
        return Result.successVoid();
    }


}

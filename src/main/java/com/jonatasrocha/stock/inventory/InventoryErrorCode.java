package com.jonatasrocha.stock.inventory;

import com.jonatasrocha.stock.common.ErrorCode;

enum InventoryErrorCode implements ErrorCode {

    INVENTORY_ALREADY_EXISTS("Already exists inventory for this product", STATUS_CONFLICT),
    INSUFICCIENT_STOCK("Product not has this quantity available in stock", STATUS_UNPROCESSABLE_CONTENT);

    private final String message;
    private final int status;

    private InventoryErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public int status() {
        return this.status;
    }

}

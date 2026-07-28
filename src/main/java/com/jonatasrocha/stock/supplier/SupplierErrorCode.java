package com.jonatasrocha.stock.supplier;

import com.jonatasrocha.stock.common.ErrorCode;

public enum SupplierErrorCode implements ErrorCode {

    SUPPLIER_NOT_FOUND("Supplier not found", STATUS_NOT_FOUND),
    SUPPLIER_CONFLICT("Already exists a supplier with this same e-mail", STATUS_CONFLICT);

    private final String message;
    private final int status;

    SupplierErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
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

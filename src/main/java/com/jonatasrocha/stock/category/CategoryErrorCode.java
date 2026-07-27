package com.jonatasrocha.stock.category;

import com.jonatasrocha.stock.common.ErrorCode;

public enum CategoryErrorCode implements ErrorCode {

    CATEGORY_NOT_FOUND("Category not found", STATUS_NOT_FOUND),
    CATEGORY_CONFLICT("This category name is already used by other category", STATUS_CONFLICT);

    private final String message;
    private final int status;

    private CategoryErrorCode(String message, int status) {
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

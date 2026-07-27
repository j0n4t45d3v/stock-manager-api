package com.jonatasrocha.stock.common;

public interface ErrorCode {

    public static final int STATUS_BAD_REQUEST = 400;  
    public static final int STATUS_NOT_FOUND = 404;  
    public static final int STATUS_CONFLICT = 409;  
    public static final int STATUS_UNPROCESSABLE_CONTENT = 422;  

    String code();
    String message();
    int status();

}

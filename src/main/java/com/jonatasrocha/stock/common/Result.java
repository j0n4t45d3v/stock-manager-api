package com.jonatasrocha.stock.common;


public record Result<T, E> (T data, E error) {

    public static <T, E> Result<T, E> success(T data) {
        return new Result<>(data, null);
    }

    public static <E> Result<Void, E> successVoid() {
        return new Result<>(null, null);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }

    public boolean isFailure() {
        return this.error != null;
    }

}

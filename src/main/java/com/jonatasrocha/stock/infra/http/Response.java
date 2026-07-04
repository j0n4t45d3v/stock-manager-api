package com.jonatasrocha.stock.infra.http;

import java.util.List;

public abstract class Response {

    private final boolean success;

    public Response(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public static <T> Response ofSuccess(T data) {
        return new SuccessResponse<T, Void>(data, null);
    }

    public static <T, M> Response ofSuccess(T data, M metadata) {
        return new SuccessResponse<T, M>(data, metadata);
    }

    public static Response ofFailure(String code, String message) {
        return new FailureResponse<Void>(code, message, List.of());
    }
    
    public static <D> Response ofFailureDetails(String code, String message, List<D> details) {
        return new FailureResponse<>(code, message, details);
    }

    public static class SuccessResponse<T, M> extends Response {

        private final T data;
        private final M meta;

        protected SuccessResponse(T data, M meta) {
            super(true);
            this.data = data;
            this.meta = meta;
        }

        public T getData() {
            return data;
        }

        public M getMeta() {
            return meta;
        }

    }

    public static class FailureResponse<D> extends Response {

        private final Error<D> error;

        protected FailureResponse(String code, String message, List<D> details) {
            super(false);
            this.error = new Error<>(code, message, details);
        }

        public Error<D> getError() {
            return this.error;
        } 

        public record Error<D>(String code, String message, List<D> details) {}

    }

}

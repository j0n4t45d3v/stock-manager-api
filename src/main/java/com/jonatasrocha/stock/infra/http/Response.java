package com.jonatasrocha.stock.infra.http;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
        return new FailureResponse<Void>(code, message, null);
    }
    
    public static <D> Response ofFailureDetails(String code, String message, D details) {
        return new FailureResponse<>(code, message, details);
    }

    @JsonPropertyOrder(value = {"success", "data", "meta"})
    public static class SuccessResponse<T, M> extends Response {

        private final T data;
        @JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonPropertyOrder(value = {"success", "error"})
    public static class FailureResponse<D> extends Response {

        private final Error<D> error;

        protected FailureResponse(String code, String message, D details) {
            super(false);
            this.error = new Error<>(code, message, details);
        }

        public Error<D> getError() {
            return this.error;
        } 

        public record Error<D>(String code, String message, D details) {}

    }

}

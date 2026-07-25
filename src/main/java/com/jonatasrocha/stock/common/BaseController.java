package com.jonatasrocha.stock.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.jonatasrocha.stock.infra.http.Response;

public class BaseController {

    protected <T, R> ResponseEntity<Response> responseCreated(T content, String locationPath, Object ...locationParams) {
        var location = UriComponentsBuilder
                        .fromPath(locationPath)
                        .buildAndExpand(locationParams)
                        .toUri();
        return ResponseEntity
            .created(location)
            .body(Response.ofSuccess(content));
    }

    protected <T, M> ResponseEntity<Response> responseOkWithMetadata(T content, M meta) {
        return ResponseEntity.ok(Response.ofSuccess(content, meta));
    }

    protected <T> ResponseEntity<Response> responseOk(T content) {
        return ResponseEntity.ok(Response.ofSuccess(content));
    }

    protected <T> ResponseEntity<Response> responseNoContent() {
        return ResponseEntity.noContent().build();
    }

    protected <T> ResponseEntity<Response> responseNotFound(
        String code,
        String message
    ) {
        return responseFail(HttpStatus.NOT_FOUND, code, message);
    }

    protected <T> ResponseEntity<Response> responseConflict(
        String code,
        String message
    ) {
        return responseFail(HttpStatus.CONFLICT, code, message);
    }

    protected <T> ResponseEntity<Response> responseFail(
        HttpStatus httpStatus,
        String code,
        String message
    ) {
        return ResponseEntity.status(httpStatus)
            .body(Response.ofFailure(code, message));
    }
    
    protected <T, D> ResponseEntity<Response> responseFail(
        HttpStatus httpStatus,
        String code,
        String message,
        D details
    ) {
        return ResponseEntity.status(httpStatus)
            .body(Response.ofFailureDetails(code, message, details));
    }
}

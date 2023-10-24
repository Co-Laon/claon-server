package com.claon.center.common.exception;

import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {
    private final int status;
    private final int errorCode;
    private final String message;

    public FeignClientException(int status, int errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}

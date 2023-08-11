package com.claon.center.common.exception;

public class UnauthorizedException extends BaseRuntimeException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
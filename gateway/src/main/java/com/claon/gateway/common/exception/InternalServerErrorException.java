package com.claon.gateway.common.exception;

public class InternalServerErrorException extends BaseRuntimeException {
    public InternalServerErrorException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

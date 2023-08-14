package com.claon.user.common.exception;

public class BadRequestException extends BaseRuntimeException{
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
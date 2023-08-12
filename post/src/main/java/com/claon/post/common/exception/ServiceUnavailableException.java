package com.claon.post.common.exception;

public class ServiceUnavailableException extends BaseRuntimeException {
    public ServiceUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

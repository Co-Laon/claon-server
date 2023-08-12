package com.claon.post.common.exception;

public class ConflictExceptionDto extends ExceptionDto {
    public ConflictExceptionDto(String message) {
        super(ErrorCode.CONFLICT_STATE, message);
    }
}

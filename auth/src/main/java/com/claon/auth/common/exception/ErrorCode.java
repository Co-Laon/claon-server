package com.claon.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    /**
     * 400 Bad Request
     */
    ROW_DOES_NOT_EXIST(40000),
    ROW_ALREADY_EXIST(40001),
    INVALID_FORMAT(40002),
    INVALID_PARAMETER(40003),
    INVALID_OAUTH2_PROVIDER(40004),
    WRONG_STORE(40005),
    WRONG_SEARCH_OPTION(40006),
    WRONG_CENTER_REPORT_TYPE(40007),
    /**
     * 401 Unauthorized Error
     */
    NOT_ACCESSIBLE(40100),
    INVALID_JWT(40101),
    NOT_SIGN_IN(40102),
    USER_DOES_NOT_EXIST(40103),
    /**
     * 404 Not Found Error
     */
    DATA_DOES_NOT_EXIST(40400),
    /**
     * 409 Conflict
     */
    CONFLICT_STATE(40900),
    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(50000),
    /**
     * 503 Service Unavailable
     */
    SERVICE_UNAVAILABLE(50300),
    ;

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }
}

package coLaon.ClaonBack.common.exception;

public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    ROW_DOES_NOT_EXIST(40000),
    ROW_ALREADY_EXIST(40001),
    WRONG_ADDRESS(40002),
    INVALID_FORMAT(40003),
    INVALID_OAUTH2_PROVIDER(40004),
    WRONG_PURPOSE(40005),
    INVALID_PARAMETER(40006),
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

    public int getCode() {
        return this.code;
    }
}

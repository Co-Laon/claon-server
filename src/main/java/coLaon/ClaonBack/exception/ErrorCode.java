package coLaon.ClaonBack.exception;

public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    ROW_ALREADY_DELETED(40000),
    /**
     *  401 Unauthorized Error
     */
    NOT_ACCESSIBLE(40100);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    private int getCode() {
        return this.code;
    }
}

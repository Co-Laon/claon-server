package coLaon.ClaonBack.exception;

public abstract class BaseRuntimeException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String message;

    public BaseRuntimeException(
            ErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }
}

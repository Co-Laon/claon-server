package coLaon.ClaonBack.exception;

import org.jetbrains.annotations.NotNull;

public abstract class BaseRuntimeException extends RuntimeException{

    @NotNull
    private final ErrorCode errorCode;

    @NotNull
    private final String message;

    public BaseRuntimeException(
            @NotNull ErrorCode errorCode,
            @NotNull String message
    ) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public @NotNull ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public @NotNull String getMessage() {
        return this.message;
    }
}

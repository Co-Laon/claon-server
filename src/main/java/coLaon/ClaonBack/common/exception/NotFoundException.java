package coLaon.ClaonBack.common.exception;

public class NotFoundException extends BaseRuntimeException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
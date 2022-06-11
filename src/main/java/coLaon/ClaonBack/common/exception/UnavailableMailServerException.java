package coLaon.ClaonBack.common.exception;

public class UnavailableMailServerException extends BaseRuntimeException {

    public UnavailableMailServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

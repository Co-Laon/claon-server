package coLaon.ClaonBack.exception;

public class InternalServerException extends BaseRuntimeException{

    public InternalServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;

public class IsDeletedValidator extends Validator{

    private final Boolean isDeleted;
    private final String target;

    public IsDeletedValidator(Boolean isDeleted, String target) {
        this.isDeleted = isDeleted;
        this.target = target;
    }

    public static IsDeletedValidator of(Boolean isDeleted, String target) {
        return new IsDeletedValidator(isDeleted, target);
    }

    @Override
    public void validate() {
        if (this.isDeleted) {
            throw new BadRequestException(ErrorCode.ROW_ALREADY_DELETED, "DELETED " + this.target);
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}
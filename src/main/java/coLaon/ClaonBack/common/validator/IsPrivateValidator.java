package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

public class IsPrivateValidator extends Validator {
    private final Boolean isPrivate;

    private IsPrivateValidator(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public static IsPrivateValidator of(Boolean isPrivate) {
        return new IsPrivateValidator(isPrivate);
    }

    @Override
    public void validate() {
        if (this.isPrivate) {
            throw new BadRequestException(
                    ErrorCode.NOT_ACCESSIBLE,
                    "비공개 계정입니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

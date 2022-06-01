package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;
import org.jetbrains.annotations.NotNull;

public class PasswordValidator extends Validator {

    private final String srcPassword;
    private final String dstPassword;

    public PasswordValidator(String srcPassword, String dstPassword) {
        this.srcPassword = srcPassword;
        this.dstPassword = dstPassword;
    }

    @Override
    public void validate() {
        if (!srcPassword.equals(dstPassword)) {
            throw new BadRequestException(ErrorCode.INVALID_SIGN_IN, "WRONG PASSWORD");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

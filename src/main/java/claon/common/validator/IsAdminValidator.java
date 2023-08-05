package claon.common.validator;

import claon.common.exception.ErrorCode;
import claon.common.exception.UnauthorizedException;

public class IsAdminValidator extends Validator {
    private final String email;
    private static final String ADMIN_EMAIL = "coraon.dev@gmail.com";

    private IsAdminValidator(String email) {
        this.email = email;
    }

    public static IsAdminValidator of(String email) {
        return new IsAdminValidator(email);
    }

    @Override
    public void validate() {
        if (!this.email.equals(IsAdminValidator.ADMIN_EMAIL)) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    "접근 권한이 없습니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

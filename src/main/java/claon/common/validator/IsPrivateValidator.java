package claon.common.validator;

import claon.common.exception.ErrorCode;
import claon.common.exception.UnauthorizedException;

public class IsPrivateValidator extends Validator {
    private final String userNickname;
    private final Boolean isPrivate;

    private IsPrivateValidator(
            String userNickname,
            Boolean isPrivate
    ) {
        this.userNickname = userNickname;
        this.isPrivate = isPrivate;
    }

    public static IsPrivateValidator of(
            String userNickname,
            Boolean isPrivate
    ) {
        return new IsPrivateValidator(userNickname, isPrivate);
    }

    @Override
    public void validate() {
        if (this.isPrivate) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    String.format("%s은 비공개 상태입니다.", userNickname)
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

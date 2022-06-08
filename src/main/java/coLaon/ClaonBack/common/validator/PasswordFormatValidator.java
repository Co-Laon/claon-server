package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordFormatValidator extends Validator{
    private final String password;

    public PasswordFormatValidator(String password) {
        this.password = password;
    }

    @Override
    public void validate() {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        if (!m.matches()) {
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

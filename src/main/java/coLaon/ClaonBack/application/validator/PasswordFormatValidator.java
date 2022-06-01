package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;

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
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "WRONG PASSWORD FORMAT");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

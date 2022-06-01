package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFormatValidator extends Validator{

    private final String userEmail;

    public EmailFormatValidator(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public void validate() {
        String regex = "^(.+)@(.+)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(userEmail);

        if (!m.matches()) {
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "WRONG EMAIL FORMAT");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberFormatValidator extends Validator {

    private final String phoneNumber;

    public PhoneNumberFormatValidator(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void validate() {
        String regex = "^\\d{3}-\\d{3,4}-\\d{4}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phoneNumber);

        if (!m.matches()) {
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "WRONG PHONE NUMBER FORMAT");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

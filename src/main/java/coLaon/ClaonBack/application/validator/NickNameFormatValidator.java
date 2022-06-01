package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.BadRequestException;
import coLaon.ClaonBack.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NickNameFormatValidator extends Validator{

    private final String nickName;

    public NickNameFormatValidator(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void validate() {
        String regex = "^[0-9a-zA-Z가-힣]{2,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nickName);

        if (!m.matches()) {
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "WRONG NICKNAME FORMAT");
        }

        if (this.next != null) {
            this.next.validate();
        }

    }
}
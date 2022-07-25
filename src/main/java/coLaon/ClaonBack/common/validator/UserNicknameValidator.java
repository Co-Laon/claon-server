package coLaon.ClaonBack.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNicknameValidator implements ConstraintValidator<UserNickname, String> {

    @Override
    public boolean isValid(String userNickname,
                           ConstraintValidatorContext cxt) {

        return (userNickname != null)
            // length: 2-20
            && (2 <= userNickname.length() && userNickname.length() <= 20)
            // check if all chars are in [0-9a-zA-Z가-힣]
            && userNickname.chars().allMatch(c -> ('0' <= c && c <= '9') // 0-9
                                             || ('a' <= c && c <= 'z') // a-z
                                             || ('A' <= c && c <= 'Z') // A-Z
                                             || ('가' <= c && c <= '힣')); // (Kor)
    }
}

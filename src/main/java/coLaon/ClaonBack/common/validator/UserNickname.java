package coLaon.ClaonBack.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = UserNicknameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserNickname {
    String message() default "이름은 길이 2~20자의 영어, 숫자, 한글 (자모 제외) 조합이어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

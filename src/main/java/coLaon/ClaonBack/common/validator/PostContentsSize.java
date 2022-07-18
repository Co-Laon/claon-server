package coLaon.ClaonBack.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PostContentsSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostContentsSize {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

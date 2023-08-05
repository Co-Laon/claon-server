package claon.post.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = ClimbingHistorySizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClimbingHistorySize {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

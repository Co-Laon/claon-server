package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.post.dto.PostContentsDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PostContentsSizeValidator implements ConstraintValidator<PostContentsSize, List<PostContentsDto>> {
    @Override
    public boolean isValid(List<PostContentsDto> value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.size() <= 10 && value.size() >= 1;
    }
}

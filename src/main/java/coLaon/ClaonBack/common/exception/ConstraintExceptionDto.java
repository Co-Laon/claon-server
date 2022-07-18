package coLaon.ClaonBack.common.exception;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ConstraintExceptionDto extends ExceptionDto {
    private final List<String> violations;

    public ConstraintExceptionDto(ErrorCode errorCode, String message, ConstraintViolationException exception) {
        super(errorCode, message);

        this.violations = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }
}

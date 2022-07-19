package coLaon.ClaonBack.common.exception;

import lombok.Getter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MethodArgumentNotValidExceptionDto extends ExceptionDto {
    private final List<String> violations;

    public MethodArgumentNotValidExceptionDto(ErrorCode errorCode, String message, MethodArgumentNotValidException exception) {
        super(errorCode, message);

        this.violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}

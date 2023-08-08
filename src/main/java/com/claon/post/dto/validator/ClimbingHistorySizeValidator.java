package com.claon.post.dto.validator;

import com.claon.post.dto.ClimbingHistoryRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ClimbingHistorySizeValidator implements ConstraintValidator<ClimbingHistorySize, List<ClimbingHistoryRequestDto>> {
    @Override
    public boolean isValid(
            List<ClimbingHistoryRequestDto> value,
            ConstraintValidatorContext context
    ) {
        if (value == null) return true;
        int size = value.stream().map(ClimbingHistoryRequestDto::getClimbingCount).reduce(0, Integer::sum);
        return size <= 10 && size > 0;
    }
}

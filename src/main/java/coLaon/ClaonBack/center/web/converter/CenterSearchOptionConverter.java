package coLaon.ClaonBack.center.web.converter;

import coLaon.ClaonBack.center.dto.CenterSearchOption;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class CenterSearchOptionConverter implements Converter<String, CenterSearchOption> {
    @Override
    public CenterSearchOption convert(String source) {
        return Arrays.stream(CenterSearchOption.values())
                .filter(v -> source.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_SEARCH_OPTION,
                                "잘못된 검색 입니다."
                        )
                );
    }
}

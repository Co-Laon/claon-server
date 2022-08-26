package coLaon.ClaonBack.user.web.converter;

import coLaon.ClaonBack.common.domain.enums.MetropolitanArea;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class MetropolitanAreaConverter implements Converter<String, MetropolitanArea> {
    @Override
    public MetropolitanArea convert(String source) {
        return Arrays.stream(MetropolitanArea.values())
                .filter(v -> source.equals(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_ADDRESS,
                                "잘못된 주소입니다."
                        )
                );
    }
}

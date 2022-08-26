package coLaon.ClaonBack.center.web.converter;

import coLaon.ClaonBack.center.domain.CenterReportType;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class CenterReportTypeConverter implements Converter<String, CenterReportType> {
    @Override
    public CenterReportType convert(String source) {
        return Arrays.stream(CenterReportType.values())
                .filter(v -> source.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_CENTER_REPORT_TYPE,
                                String.format("'%s' is invalid : not supported", source)
                        )
                );
    }
}

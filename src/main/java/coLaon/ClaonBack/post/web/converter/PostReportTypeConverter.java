package coLaon.ClaonBack.post.web.converter;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.enums.PostReportType;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class PostReportTypeConverter implements Converter<String, PostReportType> {
    @Override
    public PostReportType convert(String source) {
        return Arrays.stream(PostReportType.values())
                .filter(rp -> source.equals(rp.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.INVALID_PARAMETER,
                                "잘못된 신고 사유입니다."
                        )
                );
    }
}

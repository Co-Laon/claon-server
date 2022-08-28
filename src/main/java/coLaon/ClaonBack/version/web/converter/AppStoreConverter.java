package coLaon.ClaonBack.version.web.converter;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.version.domain.AppStore;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class AppStoreConverter implements Converter<String, AppStore> {
    @Override
    public AppStore convert(String source) {
        return Arrays.stream(AppStore.values())
                .filter(v -> source.equals(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_STORE,
                                "지원하지 않는 스토어 입니다."
                        )
                );
    }
}

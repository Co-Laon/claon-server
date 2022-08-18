package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.List;

public class IsHoldValidator extends Validator {
    private final String srcId;
    private final List<String> dstIds;

    public IsHoldValidator(String srcId, List<String> dstIds) {
        this.srcId = srcId;
        this.dstIds = dstIds;
    }

    public static IsHoldValidator of(String srcId, List<String> dstIds) {
        return new IsHoldValidator(srcId, dstIds);
    }

    @Override
    public void validate() {
        if(!dstIds.contains(srcId)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_PARAMETER,
                    "잘못된 홀드 정보입니다."
            );
        }
    }
}

package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;

import java.util.UUID;

public class IdEqualValidator extends Validator {

    private final UUID srcId;
    private final UUID dstId;

    public IdEqualValidator(UUID srcId, UUID dstId) {
        this.srcId = srcId;
        this.dstId = dstId;
    }

    public static IdEqualValidator of(UUID srcId, UUID dstId) {
        return new IdEqualValidator(srcId, dstId);
    }

    @Override
    public void validate() {
        if (!srcId.equals(dstId)) {
            throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

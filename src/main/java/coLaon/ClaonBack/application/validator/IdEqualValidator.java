package coLaon.ClaonBack.application.validator;

import coLaon.ClaonBack.exception.ErrorCode;
import coLaon.ClaonBack.exception.UnauthorizedException;

import java.util.UUID;

public class IdEqualValidator extends Validator {

    private final UUID srcId;
    private final UUID dstId;

    public IdEqualValidator(UUID srcId, UUID dstId) {
        this.srcId = srcId;
        this.dstId = dstId;
    }

    @Override
    public void validate() {
        if (!srcId.equals(dstId)) {
            throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "NO PERMISSION");
        }

        this.next.ifPresent(Validator::validate);
    }
}

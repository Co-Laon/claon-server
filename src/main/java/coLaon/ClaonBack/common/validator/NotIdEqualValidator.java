package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;

public class NotIdEqualValidator extends Validator {
    private final String srcId;
    private final String dstId;
    private final String domain;

    public NotIdEqualValidator(String srcId, String dstId, String domain) {
        this.srcId = srcId;
        this.dstId = dstId;
        this.domain = domain;
    }

    public static NotIdEqualValidator of(String srcId, String dstId, String domain) {
        return new NotIdEqualValidator(srcId, dstId, domain);
    }

    @Override
    public void validate() {
        if (srcId.equals(dstId)) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    String.format("자기 자신은 %s이 불가능합니다.", domain)
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

package com.claon.common.validator;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.UnauthorizedException;

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
                    String.format("자신을 %s할 수 없습니다.", domain)
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

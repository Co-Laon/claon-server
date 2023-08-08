package com.claon.common.validator;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.NotFoundException;
import com.claon.post.domain.PostContents;

import java.util.List;

public class IsExistUrlValidator extends Validator{

    private final List<PostContents> contentsList;
    private final String targetUrl;

    public IsExistUrlValidator(List<PostContents> contentsList, String targetUrl) {
        this.contentsList = contentsList;
        this.targetUrl = targetUrl;
    }

    public static IsExistUrlValidator of(List<PostContents> contentsList, String targetUrl) {
        return new IsExistUrlValidator(contentsList, targetUrl);
    }

    @Override
    public void validate() {
        if (contentsList.stream()
                .noneMatch(s -> s.getUrl().contains(targetUrl))
        ) {
            throw new NotFoundException(
                    ErrorCode.DATA_DOES_NOT_EXIST,
                    "게시글 이미지를 찾을 수 없습니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

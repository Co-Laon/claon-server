package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.PostContents;

import java.util.Set;

public class ContentsCountValidator extends Validator{

    private final Set<PostContents> postContents;

    public ContentsCountValidator(Set<PostContents> postContents) {
        this.postContents = postContents;
    }

    public static ContentsCountValidator of(Set<PostContents> postContents) {
        return new ContentsCountValidator(postContents);
    }

    @Override
    public void validate() {
        if (postContents.size() > 10) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "이미지 혹은 영상은 최대 10개까지 업로드 할 수 있습니다.");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

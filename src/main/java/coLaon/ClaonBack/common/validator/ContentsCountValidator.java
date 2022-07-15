package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.PostContents;
import java.util.List;
import java.util.Set;


public class ContentsCountValidator extends Validator{

    private final List<PostContents> postContents;

    public ContentsCountValidator(List<PostContents> postContents) {
        this.postContents = postContents;
    }

    public static ContentsCountValidator of(List<PostContents> postContents) {
        return new ContentsCountValidator(postContents);
    }

    @Override
    public void validate() {
        if (postContents.size() > 10 || postContents.size() < 1) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "이미지 혹은 영상은 1개 이상 10개 이하 업로드해야 합니다.");
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

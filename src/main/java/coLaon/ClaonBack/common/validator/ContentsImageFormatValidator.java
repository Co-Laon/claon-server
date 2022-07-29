package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.dto.PostContentsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContentsImageFormatValidator extends Validator {
    private final List<PostContentsDto> postContents;
    private final List<String> imageFormat = new ArrayList<>(Arrays.asList("png", "jpg", "jpeg"));

    public ContentsImageFormatValidator(List<PostContentsDto> postContents) {
        this.postContents = postContents;
    }

    public static ContentsImageFormatValidator of(List<PostContentsDto> postContents) {
        return new ContentsImageFormatValidator(postContents);
    }

    @Override
    public void validate() {
        List<Boolean> postContentsUrls = postContents.stream()
                .map(PostContentsDto::getUrl)
                .map(url -> url.substring(url.lastIndexOf(".") + 1))
                .map(String::toLowerCase)
                .map(imageFormat::contains)
                .collect(Collectors.toList());

        if (postContentsUrls.contains(false)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "이미지 형식이 잘못되었습니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

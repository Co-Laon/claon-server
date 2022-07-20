package coLaon.ClaonBack.common.validator;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class IsImageValidator extends Validator{
    private final MultipartFile multipartFile;

    private IsImageValidator(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public static IsImageValidator of(MultipartFile multipartFile) {
        return new IsImageValidator(multipartFile);
    }

    @Override
    public void validate() {
        try {
            if (Files.probeContentType(Path.of(Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .startsWith("image")) {
                throw new BadRequestException(
                        ErrorCode.INVALID_FORMAT,
                        "이미지 파일 형식이 올바르지 않습니다."
                );
            }
        } catch (IOException e) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "이미지 파일이 올바르지 않습니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

package claon.common.validator;

import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class IsImageValidator extends Validator {
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
            if (!Files.probeContentType(Path.of(Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .startsWith("image")) {
                throw new BadRequestException(
                        ErrorCode.INVALID_FORMAT,
                        "이미지만 업로드 가능합니다."
                );
            }
        } catch (IOException e) {
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "잘못된 파일입니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}

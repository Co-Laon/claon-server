package claon.post.infra;

import claon.common.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostContentsImageManager {
    private final S3Util s3Util;
    private final String imageContentsDirName = "post/image";

    public String uploadContents(MultipartFile image) {
        String fileExtension = Objects.requireNonNull(image.getContentType())
                .substring(image.getContentType().lastIndexOf("/") + 1);
        String fileName = this.imageContentsDirName + "/" + LocalDate.now() + "/" + UUID.randomUUID() + "." + fileExtension;

        return this.s3Util.uploadImage(image, fileName, fileExtension);
    }

    public void deleteContents(String imagePath) {
        this.s3Util.deleteImage(imagePath);
    }
}

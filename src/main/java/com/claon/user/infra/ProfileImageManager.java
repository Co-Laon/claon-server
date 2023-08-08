package com.claon.user.infra;

import com.claon.common.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileImageManager {
    private final S3Util s3Util;
    private final String profileDirName = "profile";

    public String uploadProfile(MultipartFile image) {
        String fileExtension = Objects.requireNonNull(image.getContentType())
                .substring(image.getContentType().lastIndexOf("/") + 1);
        String fileName = this.profileDirName + "/" + LocalDate.now() + "/" + UUID.randomUUID() + "." + fileExtension;

        return this.s3Util.uploadImage(image, fileName, fileExtension);
    }

    public void deleteProfile(String imagePath) {
        this.s3Util.deleteImage(imagePath);
    }
}

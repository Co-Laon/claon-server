package coLaon.ClaonBack.storage.service;

import coLaon.ClaonBack.common.utils.S3Util;
import coLaon.ClaonBack.common.validator.IsImageValidator;
import coLaon.ClaonBack.storage.domain.enums.ImagePurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final S3Util s3Util;

    public String imageUpload(MultipartFile multipartFile, String purpose) {
        IsImageValidator.of(multipartFile).validate();

        return s3Util.upload(multipartFile, ImagePurpose.of(purpose));
    }
}

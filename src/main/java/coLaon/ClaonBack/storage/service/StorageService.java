package coLaon.ClaonBack.storage.service;

import coLaon.ClaonBack.common.infrastructure.S3Uploader;
import coLaon.ClaonBack.common.validator.IsImageValidator;
import coLaon.ClaonBack.storage.domain.enums.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final S3Uploader s3Uploader;

    public String upload(MultipartFile multipartFile, String dirName) {
        IsImageValidator.of(multipartFile).validate();

        return s3Uploader.upload(multipartFile, Purpose.of(dirName));
    }
}

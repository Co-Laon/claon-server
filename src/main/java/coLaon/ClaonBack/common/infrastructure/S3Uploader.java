package coLaon.ClaonBack.common.infrastructure;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {
        File uploadFile = convert(multipartFile).orElseThrow(
                () -> new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "파일 전환에 실패했습니다."
                )
        );
        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + LocalDate.now() + "/" + UUID.randomUUID() + "." +
                uploadFile.getName().substring(uploadFile.getName().lastIndexOf(".") + 1);
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile multipartFile) {
        File convertFile = new File(
                System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename()
        );

        try{
            if (convertFile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(convertFile);
                fos.write(multipartFile.getBytes());
            }
            else {
                return Optional.empty();
            }
        }
        catch (IOException e) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "파일 전환에 실패했습니다."
            );
        }
        return Optional.of(convertFile);
    }
}
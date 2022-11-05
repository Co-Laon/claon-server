package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {
    private final AmazonS3Client amazonS3Client;
    private final ImageUtil imageUtil;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadImage(MultipartFile uploadFile, String fileName, String fileExtension) {
        if(amazonS3Client.doesObjectExist(bucket, fileName)) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "S3 객체 업로드를 실패했습니다."
            );
        }

        MultipartFile resizedFile = imageUtil.resizeImage(uploadFile, fileExtension, fileName);
        ObjectMetadata objectMetadata = getObjectMetadata(uploadFile, resizedFile);

        return putS3(resizedFile, fileName, objectMetadata);
    }

    private String putS3(MultipartFile resizedFile, String fileName, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = resizedFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "S3 객체 업로드를 실패했습니다."
            );
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file, MultipartFile resizedFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedFile.getSize());
        objectMetadata.setContentType(file.getContentType());

        return objectMetadata;
    }

    public void deleteImage(String imagePath) {
        String domainName = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
        String key = imagePath.substring(imagePath.indexOf(imagePath.split(domainName)[1]));
        
        if (!amazonS3Client.doesObjectExist(bucket, key)) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "S3 객체 삭제를 실패했습니다."
            );
        }

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
        this.amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
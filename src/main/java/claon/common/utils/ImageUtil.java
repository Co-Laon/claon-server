package claon.common.utils;

import claon.common.exception.ErrorCode;
import claon.common.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final int RESIZED_LENGTH = 1920;

    public MultipartFile resizeImage(MultipartFile file, String fileFormatName, String fileName) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            if ((originalWidth < RESIZED_LENGTH && originalWidth > originalHeight) ||
                    (originalHeight < RESIZED_LENGTH && originalHeight > originalWidth)) {
                return file;
            }

            MarvinImage marvinImage = new MarvinImage(image, fileFormatName);
            scaleImage(marvinImage, originalWidth, originalHeight);

            BufferedImage bufferedImage = marvinImage.getBufferedImageNoAlpha();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, fileFormatName, byteArrayOutputStream);
            byteArrayOutputStream.flush();

            return new MockMultipartFile(fileName, byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "파일 리사이즈를 실패했습니다."
            );
        }
    }

    private void scaleImage(MarvinImage marvinImage, int width, int height) {
        Scale scale = new Scale();
        scale.load();

        if (width >= height) {
            scale.setAttribute("newWidth", RESIZED_LENGTH);
            scale.setAttribute("newHeight", RESIZED_LENGTH * height / width);
        }

        else {
            scale.setAttribute("newWidth", RESIZED_LENGTH * width / height);
            scale.setAttribute("newHeight", RESIZED_LENGTH);
        }

        scale.process(
                marvinImage.clone(),
                marvinImage,
                null,
                null,
                false
        );
    }
}

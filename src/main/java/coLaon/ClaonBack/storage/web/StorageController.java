package coLaon.ClaonBack.storage.web;

import coLaon.ClaonBack.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/image/{purpose}")
    public String upload(
            @RequestPart MultipartFile image,
            @PathVariable String purpose
    ) {
        return storageService.imageUpload(image, purpose);
    }
}

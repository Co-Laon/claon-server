package coLaon.ClaonBack.version.web;

import coLaon.ClaonBack.version.dto.AppVersionFindResponseDto;
import coLaon.ClaonBack.version.service.AppVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app-versions")
public class AppVersionController {
    private final AppVersionService appVersionService;

    @GetMapping(value = "/{key}/apple")
    @ResponseStatus(value = HttpStatus.OK)
    public AppVersionFindResponseDto getAppleVersion(
            @PathVariable String key
    ) {
        return this.appVersionService.findAppleVersion(key);
    }

    @GetMapping(value = "/{key}/android")
    @ResponseStatus(value = HttpStatus.OK)
    public AppVersionFindResponseDto getAndroidVersion(
            @PathVariable String key
    ) {
        return this.appVersionService.findAndroidVersion(key);
    }
}

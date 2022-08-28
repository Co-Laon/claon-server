package coLaon.ClaonBack.version.web;

import coLaon.ClaonBack.version.domain.AppStore;
import coLaon.ClaonBack.version.dto.AppVersionFindResponseDto;
import coLaon.ClaonBack.version.service.AppVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app-versions")
public class AppVersionController {
    private final AppVersionService appVersionService;

    @GetMapping(value = "/{store}")
    @ResponseStatus(value = HttpStatus.OK)
    public AppVersionFindResponseDto getAppleVersion(
            @PathVariable AppStore store
    ) {
        return this.appVersionService.findVersion(store);
    }
}

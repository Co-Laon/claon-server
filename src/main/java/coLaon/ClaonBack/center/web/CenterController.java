package coLaon.ClaonBack.center.web;

import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.service.CenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/centers")
public class CenterController {
    private final CenterService centerService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public CenterResponseDto create(
            @AuthenticationPrincipal String userId,
            @RequestBody CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userId, centerCreateRequestDto);
    }
}

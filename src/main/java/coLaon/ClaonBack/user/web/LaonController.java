package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.dto.LaonResponseDto;
import coLaon.ClaonBack.user.service.LaonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class LaonController {
    private final LaonService laonService;

    @PostMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LaonResponseDto laon(
            @AuthenticationPrincipal String userId,
            @PathVariable String laonId) {
        return this.laonService.laon(laonId, userId);
    }

    @DeleteMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.OK)
    public LaonResponseDto unlaon(
            @AuthenticationPrincipal String userId,
            @PathVariable String laonId) {
        return this.laonService.unlaon(laonId, userId);
    }

}

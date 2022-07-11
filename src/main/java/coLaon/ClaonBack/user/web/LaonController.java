package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.service.LaonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private final LaonService laonService;

    @PostMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createLaon(
            @AuthenticationPrincipal String userId,
            @PathVariable String laonId) {
        this.laonService.createLaon(laonId, userId);
    }

    @DeleteMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLaon(
            @AuthenticationPrincipal String userId,
            @PathVariable String laonId) {
        this.laonService.deleteLaon(laonId, userId);
    }
}

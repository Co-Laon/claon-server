package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.dto.FollowResponseDto;
import coLaon.ClaonBack.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class FollowController {
    private final FollowService followService;

    @PostMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public FollowResponseDto follow(
            @RequestHeader(value = "userId") String userId,
            @PathVariable String laonId) {
        return this.followService.follow(userId, laonId);
    }

    @DeleteMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.OK)
    public FollowResponseDto unfollow(
            @RequestHeader(value = "userId") String userId,
            @PathVariable String laonId) {
        return this.followService.unfollow(userId, laonId);
    }

}

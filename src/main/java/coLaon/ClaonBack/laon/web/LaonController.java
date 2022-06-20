package coLaon.ClaonBack.laon.web;

import coLaon.ClaonBack.laon.Service.LaonService;
import coLaon.ClaonBack.laon.dto.LikeRequestDto;
import coLaon.ClaonBack.laon.dto.LikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private final LaonService laonService;

    @PostMapping("/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto) {
        return this.laonService.createLike(userId, likeRequestDto);
    }
}

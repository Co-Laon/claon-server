package coLaon.ClaonBack.laon.web;

import coLaon.ClaonBack.laon.Service.LaonCommentService;
import coLaon.ClaonBack.laon.Service.LaonService;
import coLaon.ClaonBack.laon.dto.*;
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
    private LaonCommentService laonCommentService;

    @PostMapping("/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto) {
        return this.laonService.createLike(userId, likeRequestDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return this.laonCommentService.createComment(userId, commentRequestDto);
    }

    @PostMapping("/{id}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LaonResponseDto createLaon(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid LaonCreateRequestDto laonCreateRequestDto){
        return this.laonService.createLaon(userId, laonCreateRequestDto);
    }
}

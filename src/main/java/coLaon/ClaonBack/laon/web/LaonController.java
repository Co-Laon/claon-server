package coLaon.ClaonBack.laon.web;

import coLaon.ClaonBack.laon.Service.LaonCommentService;
import coLaon.ClaonBack.laon.Service.LaonService;
import coLaon.ClaonBack.laon.dto.CommentRequestDto;
import coLaon.ClaonBack.laon.dto.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private LaonService laonService;
    private LaonCommentService laonCommentService;

    @PostMapping("/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return this.laonCommentService.createComment(userId, commentRequestDto);
    }
}

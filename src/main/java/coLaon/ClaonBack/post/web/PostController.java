package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.post.Service.PostCommentService;
import coLaon.ClaonBack.post.Service.PostService;
import coLaon.ClaonBack.post.dto.CommentRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;
    private final PostCommentService postCommentService;

    @PostMapping("/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto) {
        return this.postService.createLike(userId, likeRequestDto);
    }

    @DeleteMapping("/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto) {
        return this.postService.deleteLike(userId, likeRequestDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        return this.postCommentService.createComment(userId, commentRequestDto);
    }
}

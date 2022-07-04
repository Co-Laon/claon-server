package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.post.Service.PostCommentService;
import coLaon.ClaonBack.post.Service.LaonService;
import coLaon.ClaonBack.post.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class PostController {
    private final LaonService laonService;
    private final PostCommentService postCommentService;

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
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto) {
        return this.postCommentService.createComment(userId, commentCreateRequestDto);
    }

    @GetMapping(value = "/comment", params = "postId")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentFindResponseDto> findAllParentComment(
            @RequestParam String postId) {
        return this.postCommentService.findCommentsByPost(postId);
    }

    @GetMapping(value = "/comment/child-comments", params = "parentId")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentFindResponseDto> findAllChildrenComment(
            @RequestParam String parentId) {
        return this.postCommentService.findAllChildCommentsByParent(parentId);
    }

    @PutMapping(value = "/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto) {
        return this.postCommentService.updateComment(userId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment", params = "commentId")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @RequestHeader(value = "userId") String userId,
            @RequestParam String commentId) {
        return this.postCommentService.deleteComment(commentId, userId);
    }


}

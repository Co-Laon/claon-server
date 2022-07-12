package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.post.service.PostCommentService;
import coLaon.ClaonBack.post.service.PostService;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostCommentService postCommentService;

    @PostMapping("/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto
    ) {
        return this.postService.createLike(userId, likeRequestDto);
    }

    @DeleteMapping("/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid LikeRequestDto likeRequestDto) {
        return this.postService.deleteLike(userId, likeRequestDto);
    }

    @GetMapping(value = "/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public List<LikeFindResponseDto> findAllLike(
            @PathVariable String postId
    ) {
        // TODO: add pagination
        return this.postService.findLikeByPost(postId);
    }

    @PostMapping("/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto
    ) {
        return this.postCommentService.createComment(userId, commentCreateRequestDto);
    }

    @GetMapping(value = "/{postId}/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentFindResponseDto> findAllParentCommentAndThreeChildComment(
            @PathVariable String postId
    ) {
        return this.postCommentService.findCommentsByPost(postId);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ChildCommentResponseDto> findAllChildrenComment(
            @PathVariable String parentId
    ) {
        // TODO: add pagination
        return this.postCommentService.findAllChildCommentsByParent(parentId);
    }

    @PutMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @AuthenticationPrincipal String userId,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto
    ) {
        return this.postCommentService.updateComment(userId, commentId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @AuthenticationPrincipal String userId,
            @PathVariable String commentId
    ) {
        return this.postCommentService.deleteComment(commentId, userId);
    }
}

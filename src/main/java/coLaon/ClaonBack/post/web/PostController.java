package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.post.Service.PostCommentService;
import coLaon.ClaonBack.post.Service.PostService;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ChildCommentResponseDto> findAllChildrenComment(  //TODO: Pagination 추가
            @PathVariable String parentId) {
        return this.postCommentService.findAllChildCommentsByParent(parentId);
    }

    @PutMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @RequestHeader(value = "userId") String userId,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto) {
        return this.postCommentService.updateComment(userId, commentId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @RequestHeader(value = "userId") String userId,
            @PathVariable String commentId) {
        return this.postCommentService.deleteComment(commentId, userId);
    }


}

package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.common.domain.Pagination;

import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.post.dto.PostUpdateRequestDto;
import coLaon.ClaonBack.post.dto.PostReportRequestDto;
import coLaon.ClaonBack.post.dto.PostReportResponseDto;
import coLaon.ClaonBack.post.service.PostLikeService;
import coLaon.ClaonBack.post.service.PostCommentService;
import coLaon.ClaonBack.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostLikeService postLikeService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostThumbnailResponseDto> getCenterPosts(
            @AuthenticationPrincipal String userId,
            @RequestParam("centerId") String centerId,
            @RequestParam(value = "holdId", required = false) Optional<String> holdId,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postService.getCenterPosts(userId, centerId, holdId, pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostResponseDto createPost(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid PostCreateRequestDto postCreateRequestDto
    ) {
        return this.postService.createPost(userId, postCreateRequestDto);
    }

    @PutMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto updatePost(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId,
            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
    ) {
        return this.postService.updatePost(userId, postId, postUpdateRequestDto);
    }

    @GetMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostDetailResponseDto getPost(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId
    ) {
        return this.postService.findPost(userId, postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto deletePost(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId
    ) {
        return this.postService.deletePost(postId, userId);
    }

    @PostMapping("/{postId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostReportResponseDto createReport(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId,
            @RequestBody PostReportRequestDto postReportRequestDto
    ) {
        return this.postService.createReport(userId, postId, postReportRequestDto);
    }

    @PostMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId
    ) {
        return this.postLikeService.createLike(userId, postId);
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId
    ) {
        return this.postLikeService.deleteLike(userId, postId);
    }

    @GetMapping(value = "/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LikeFindResponseDto> findAllLike(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 20) final Pageable pageable
    ) {
        return this.postLikeService.findLikeByPost(userId, postId, pageable);
    }

    @PostMapping("/{postId}/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId,
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto
    ) {
        return this.postCommentService.createComment(userId, postId, commentCreateRequestDto);
    }

    @GetMapping(value = "/{postId}/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CommentFindResponseDto> findAllParentCommentAndThreeChildComment(
            @AuthenticationPrincipal String userId,
            @PathVariable String postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 10) final Pageable pageable
    ) {
        return this.postCommentService.findCommentsByPost(userId, postId, pageable);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<ChildCommentResponseDto> findAllChildrenComment(
            @AuthenticationPrincipal String userId,
            @PathVariable String parentId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 10) final Pageable pageable
    ) {
        return this.postCommentService.findAllChildCommentsByParent(userId, parentId, pageable);
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
        return this.postCommentService.deleteComment(userId, commentId);
    }
}
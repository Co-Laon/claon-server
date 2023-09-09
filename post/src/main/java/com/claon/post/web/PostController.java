package com.claon.post.web;

import com.claon.post.common.annotation.RequestUser;
import com.claon.post.common.domain.Pagination;
import com.claon.post.dto.*;
import com.claon.post.service.PostCommentService;
import com.claon.post.service.PostLikeService;
import com.claon.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostLikeService postLikeService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostResponseDto createPost(
            @RequestUser String userId,
            @RequestBody @Valid PostCreateRequestDto postCreateRequestDto
    ) {
        return this.postService.createPost(userId, postCreateRequestDto);
    }

    @PutMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto updatePost(
            @RequestUser String userId,
            @PathVariable String postId,
            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
    ) {
        return this.postService.updatePost(userId, postId, postUpdateRequestDto);
    }

    @GetMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostDetailResponseDto getPost(
            @RequestUser String userId,
            @PathVariable String postId
    ) {
        return this.postService.findPost(userId, postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto deletePost(
            @RequestUser String userId,
            @PathVariable String postId
    ) {
        return this.postService.deletePost(userId, postId);
    }

    @PostMapping("/{postId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostReportResponseDto createReport(
            @RequestUser String userId,
            @PathVariable String postId,
            @RequestBody @Valid PostReportRequestDto postReportRequestDto
    ) {
        return this.postService.createReport(userId, postId, postReportRequestDto);
    }

    @PostMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @RequestUser String userId,
            @PathVariable String postId
    ) {
        return this.postLikeService.createLike(userId, postId);
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @RequestUser String userId,
            @PathVariable String postId
    ) {
        return this.postLikeService.deleteLike(userId, postId);
    }

    @GetMapping(value = "/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LikeFindResponseDto> findAllLike(
            @RequestUser String userId,
            @PathVariable String postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return this.postLikeService.findLikeByPost(userId, postId, pageable);
    }

    @PostMapping("/{postId}/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestUser String userId,
            @PathVariable String postId,
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto
    ) {
        return this.postCommentService.createComment(userId, postId, commentCreateRequestDto);
    }

    @GetMapping(value = "/{postId}/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CommentFindResponseDto> findAllParentComment(
            @RequestUser String userId,
            @PathVariable String postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postCommentService.findCommentsByPost(userId, postId, pageable);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<ChildCommentResponseDto> findAllChildrenComment(
            @RequestUser String userId,
            @PathVariable String parentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postCommentService.findAllChildCommentsByParent(userId, parentId, pageable);
    }

    @PutMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @RequestUser String userId,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto
    ) {
        return this.postCommentService.updateComment(userId, commentId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @RequestUser String userId,
            @PathVariable String commentId
    ) {
        return this.postCommentService.deleteComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getPosts(
            @RequestUser String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findPosts(userId, pageable);
    }

    @GetMapping("/history")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getUserPostsByCenterAndYearMonth(
            @RequestUser String userId,
            @RequestParam @NotBlank String nickname,
            @RequestParam @NotBlank String centerId,
            @RequestParam @Min(2000) @Max(9999) Integer year,
            @RequestParam @Min(1) @Max(12) Integer month,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postService.findUserPostsByCenterAndYearMonth(userId, nickname, centerId, year, month, pageable);
    }
}
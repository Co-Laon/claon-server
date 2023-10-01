package com.claon.post.web;

import com.claon.post.common.annotation.RequestUser;
import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.dto.*;
import com.claon.post.dto.request.*;
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

import java.util.Optional;

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
            @RequestUser RequestUserInfo userInfo,
            @RequestBody @Valid PostCreateRequestDto postCreateRequestDto
    ) {
        return this.postService.createPost(userInfo, postCreateRequestDto);
    }

    @PutMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto updatePost(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId,
            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
    ) {
        return this.postService.updatePost(userInfo, postId, postUpdateRequestDto);
    }

    @GetMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostDetailResponseDto getPost(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId
    ) {
        return this.postService.findPost(userInfo, postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto deletePost(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId
    ) {
        return this.postService.deletePost(userInfo, postId);
    }

    @PostMapping("/{postId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostReportResponseDto createReport(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId,
            @RequestBody @Valid PostReportRequestDto postReportRequestDto
    ) {
        return this.postService.createReport(userInfo, postId, postReportRequestDto);
    }

    @PostMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId
    ) {
        return this.postLikeService.createLike(userInfo, postId);
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId
    ) {
        return this.postLikeService.deleteLike(userInfo, postId);
    }

    @GetMapping(value = "/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LikerResponseDto> findAllLike(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return this.postLikeService.findLikeByPost(userInfo, postId, pageable);
    }

    @PostMapping("/{postId}/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId,
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto
    ) {
        return this.postCommentService.createComment(userInfo, postId, commentCreateRequestDto);
    }

    @GetMapping(value = "/{postId}/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CommentDetailResponseDto> findAllParentComment(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postCommentService.findCommentsByPost(userInfo, postId, pageable);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<ChildCommentResponseDto> findAllChildrenComment(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String parentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postCommentService.findAllChildCommentsByParent(userInfo, parentId, pageable);
    }

    @PutMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto
    ) {
        return this.postCommentService.updateComment(userInfo, commentId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String commentId
    ) {
        return this.postCommentService.deleteComment(userInfo, commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getPosts(
            @RequestUser RequestUserInfo userInfo,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findPosts(userInfo, pageable);
    }

    @GetMapping("/history")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getUserPostsByCenterAndYearMonth(
            @RequestUser RequestUserInfo userInfo,
            @RequestParam @NotBlank String centerId,
            @RequestParam @Min(2000) @Max(9999) Integer year,
            @RequestParam @Min(1) @Max(12) Integer month,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postService.findUserPostsByCenterAndYearMonth(userInfo, centerId, year, month, pageable);
    }

    @GetMapping("/thumbnails")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostThumbnailResponseDto> findPostThumbnails(
            @RequestUser RequestUserInfo userInfo,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findPostThumbnailsByUser(userInfo, pageable);
    }

    @GetMapping("/centers/{centerId}/thumbnails")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostThumbnailResponseDto> findCenterPostThumbnails(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId,
            @RequestParam(value = "holdId", required = false) Optional<String> holdId,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findCenterPostThumbnailsByUser(userInfo, centerId, holdId, pageable);
    }

    @GetMapping("/centers/{centerId}/count")
    @ResponseStatus(value = HttpStatus.OK)
    public Long countPostsByCenter(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId
    ) {
        return this.postService.countPostByCenter(userInfo, centerId);
    }
}
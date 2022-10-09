package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.UserDetails;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PostCreateRequestDto postCreateRequestDto
    ) {
        return this.postService.createPost(userDetails.getUser(), postCreateRequestDto);
    }

    @PutMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto updatePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto
    ) {
        return this.postService.updatePost(userDetails.getUser(), postId, postUpdateRequestDto);
    }

    @GetMapping(value = "/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostDetailResponseDto getPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId
    ) {
        return this.postService.findPost(userDetails.getUser(), postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public PostResponseDto deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId
    ) {
        return this.postService.deletePost(userDetails.getUser(), postId);
    }

    @PostMapping("/{postId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PostReportResponseDto createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @RequestBody PostReportRequestDto postReportRequestDto
    ) {
        return this.postService.createReport(userDetails.getUser(), postId, postReportRequestDto);
    }

    @PostMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeResponseDto createLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId
    ) {
        return this.postLikeService.createLike(userDetails.getUser(), postId);
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public LikeResponseDto deleteLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId
    ) {
        return this.postLikeService.deleteLike(userDetails.getUser(), postId);
    }

    @GetMapping(value = "/{postId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LikeFindResponseDto> findAllLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 20) final Pageable pageable
    ) {
        return this.postLikeService.findLikeByPost(userDetails.getUser(), postId, pageable);
    }

    @PostMapping("/{postId}/comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto
    ) {
        return this.postCommentService.createComment(userDetails.getUser(), postId, commentCreateRequestDto);
    }

    @GetMapping(value = "/{postId}/comment")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CommentFindResponseDto> findAllParentCommentAndThreeChildComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 10) final Pageable pageable
    ) {
        return this.postCommentService.findCommentsByPost(userDetails.getUser(), postId, pageable);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<ChildCommentResponseDto> findAllChildrenComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String parentId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 10) final Pageable pageable
    ) {
        return this.postCommentService.findAllChildCommentsByParent(userDetails.getUser(), parentId, pageable);
    }

    @PutMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequestDto updateRequestDto
    ) {
        return this.postCommentService.updateComment(userDetails.getUser(), commentId, updateRequestDto);
    }

    @DeleteMapping(value = "/comment/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentResponseDto deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String commentId
    ) {
        return this.postCommentService.deleteComment(userDetails.getUser(), commentId);
    }
}
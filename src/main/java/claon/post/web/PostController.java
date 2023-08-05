package claon.post.web;

import claon.common.domain.Pagination;
import claon.post.dto.CommentCreateRequestDto;
import claon.post.dto.PostContentsUrlDto;
import claon.post.dto.PostCreateRequestDto;
import claon.post.dto.PostDetailResponseDto;
import claon.post.dto.PostReportRequestDto;
import claon.post.service.PostCommentService;
import claon.post.service.PostLikeService;
import claon.post.service.PostService;
import claon.user.domain.UserDetails;
import claon.post.dto.ChildCommentResponseDto;
import claon.post.dto.CommentFindResponseDto;
import claon.post.dto.CommentResponseDto;
import claon.post.dto.CommentUpdateRequestDto;
import claon.post.dto.LikeFindResponseDto;
import claon.post.dto.LikeResponseDto;
import claon.post.dto.PostReportResponseDto;
import claon.post.dto.PostResponseDto;
import claon.post.dto.PostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
            @RequestBody @Valid PostReportRequestDto postReportRequestDto
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
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
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
    public Pagination<CommentFindResponseDto> findAllParentComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postCommentService.findCommentsByPost(userDetails.getUser(), postId, pageable);
    }

    @GetMapping(value = "/comment/{parentId}/children")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<ChildCommentResponseDto> findAllChildrenComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String parentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
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

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findPosts(userDetails.getUser(), pageable);
    }

    @PostMapping(value = "/contents")
    @ResponseStatus(value = HttpStatus.OK)
    public String uploadContents(
            @RequestPart MultipartFile image
    ) {
        return this.postService.uploadContents(image);
    }

    @DeleteMapping(value = "/{postId}/contents")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteContents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @RequestHeader(value = "contents-url") @Valid PostContentsUrlDto postContentsUrlDto
    ) {
        this.postService.deleteContents(userDetails.getUser(), postId, postContentsUrlDto);
    }

    @GetMapping("/history")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getUserPostsByCenterAndYearMonth(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @NotBlank String nickname,
            @RequestParam @NotBlank String centerId,
            @RequestParam @Min(2000) @Max(9999) Integer year,
            @RequestParam @Min(1) @Max(12) Integer month,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        return this.postService.findUserPostsByCenterAndYearMonth(userDetails.getUser(), nickname, centerId, year, month, pageable);
    }
}
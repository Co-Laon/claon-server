package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.UserDetails;
import coLaon.ClaonBack.post.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/writers")
public class WriterController {
    private final PostService postService;

    @GetMapping("/{nickname}/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostThumbnailResponseDto> getIndividualUserPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickname,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postService.getUserPosts(userDetails.getUser(), nickname, pageable);
    }
}

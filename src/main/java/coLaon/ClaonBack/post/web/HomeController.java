package coLaon.ClaonBack.post.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.UserDetails;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {
    private final PostService postService;

    @GetMapping("/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getHomePost(
            @AuthenticationPrincipal UserDetails UserDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
       return this.postService.findHomePost(UserDetails.getUser(), pageable);
    }

    @GetMapping("/laon/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostDetailResponseDto> getHomeLaonPost(
            @AuthenticationPrincipal UserDetails UserDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.postService.findHomeLaonPost(UserDetails.getUser(), pageable);
    }
}

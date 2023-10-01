package com.claon.user.service.client;

import com.claon.user.common.domain.Pagination;
import com.claon.user.service.client.dto.UserPostInfoResponse;
import com.claon.user.service.client.dto.PostThumbnailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "post-service")
public interface PostClient {
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(
            @RequestHeader(name = "X-USER-ID") String userId
    );

    @GetMapping("/api/v1/posts/thumbnails")
    Pagination<PostThumbnailResponse> findPostThumbnails(
            @RequestHeader(name = "X-USER-ID") String userId,
            Pageable pageable
    );
}

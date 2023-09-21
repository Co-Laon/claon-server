package com.claon.center.service.client;

import com.claon.center.common.domain.Pagination;
import com.claon.center.dto.CenterPostThumbnailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "post-service")
public interface PostClient {
    @GetMapping("/api/v1/posts/centers/{centerId}/thumbnails")
    Pagination<CenterPostThumbnailResponseDto> findPostThumbnails(@RequestHeader(name = "X-USER-ID") String userId, @PathVariable String centerId, Optional<String> holdId, Pageable pageable);

    @GetMapping("/api/v1/posts/centers/{centerId}/count")
    Long countPostsByCenterId(@RequestHeader(name = "X-USER-ID") String userId, @PathVariable String centerId);
}

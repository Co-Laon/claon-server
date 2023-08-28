package com.claon.user.web;

import com.claon.user.common.domain.Pagination;
import com.claon.user.dto.LaonFindResponseDto;
import com.claon.user.dto.UserPostDetailResponseDto;
import com.claon.user.service.LaonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private final LaonService laonService;

    @PostMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createLaon(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String laonNickname
    ) {
        this.laonService.createLaon(userId, laonNickname);
    }

    @DeleteMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLaon(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String laonNickname
    ) {
        this.laonService.deleteLaon(userId, laonNickname);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LaonFindResponseDto> findAllLaon(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return this.laonService.findAllLaon(userId, pageable);
    }

    @GetMapping("/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPostDetailResponseDto> getLaonPost(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.laonService.findLaonPost(userId, pageable);
    }
}

package com.claon.user.web;

import com.claon.user.common.annotation.RequestUser;
import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.dto.LaonFindResponseDto;
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

    @PostMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createLaon(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String laonId
    ) {
        this.laonService.createLaon(userInfo, laonId);
    }

    @DeleteMapping(value = "/{laonId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLaon(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String laonId
    ) {
        this.laonService.deleteLaon(userInfo, laonId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LaonFindResponseDto> findAllLaon(
            @RequestUser RequestUserInfo userInfo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return this.laonService.findAllLaon(userInfo, pageable);
    }
}

package com.claon.center.web;

import com.claon.center.common.annotation.RequestUser;
import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.*;
import com.claon.center.service.CenterBookmarkService;
import com.claon.center.service.CenterReviewService;
import com.claon.center.service.CenterService;
import com.claon.center.common.domain.Pagination;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/centers")
public class CenterController {
    private final CenterService centerService;
    private final CenterReviewService centerReviewService;
    private final CenterBookmarkService centerBookmarkService;

    @GetMapping(value = "/{centerId}/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPostThumbnailResponseDto> getCenterPosts(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId,
            @RequestParam(value = "holdId", required = false) Optional<String> holdId,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return centerService.getCenterPosts(userInfo, centerId, holdId, pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterResponseDto create(
            @RequestUser RequestUserInfo userInfo,
            @RequestBody @Valid CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userInfo, centerCreateRequestDto);
    }

    @GetMapping(value = "/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CenterDetailResponseDto findCenter(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId
    ) {
        return this.centerService.findCenter(userInfo, centerId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPreviewResponseDto> getCenterList(
            @RequestUser RequestUserInfo userInfo,
            @RequestParam("option") CenterSearchOption option,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.centerService.findCenterListByOption(userInfo, option, pageable);
    }

    @GetMapping(value = "/name/{keyword}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CenterNameResponseDto> searchCenterName(
            @PathVariable String keyword
    ) {
        return this.centerService.searchCenterName(keyword);
    }

    @GetMapping(value = "/{centerId}/hold")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CenterHoldInfoResponseDto> findHoldInfoByCenter(
            @PathVariable String centerId
    ) {
        return this.centerService.findHoldInfoByCenterId(centerId);
    }

    @PostMapping("/{centerId}/review")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ReviewResponseDto createReview(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId,
            @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        return this.centerReviewService.createReview(userInfo, centerId, reviewCreateRequestDto);
    }

    @PutMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto updateReview(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto updateRequestDto
    ) {
        return this.centerReviewService.updateReview(userInfo, reviewId, updateRequestDto);
    }

    @DeleteMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto deleteReview(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String reviewId
    ) {
        return this.centerReviewService.deleteReview(userInfo, reviewId);
    }

    @GetMapping(value = "/{centerId}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewBundleFindResponseDto findReviewByCenter(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        return this.centerReviewService.findReview(userInfo, centerId, pageable);
    }

    @PostMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterBookmarkResponseDto create(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId
    ) {
        return this.centerBookmarkService.create(userInfo, centerId);
    }

    @DeleteMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId
    ) {
        this.centerBookmarkService.delete(userInfo, centerId);
    }

    @PostMapping(value = "/{centerId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterReportResponseDto createReport(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String centerId,
            @RequestBody @Valid CenterReportCreateRequestDto centerReportCreateRequestDto
    ) {
        return this.centerService.createReport(userInfo, centerId, centerReportCreateRequestDto);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPreviewResponseDto> searchCenter(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.centerService.searchCenter(name, pageable);
    }
}

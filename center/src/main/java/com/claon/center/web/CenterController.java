package com.claon.center.web;

import com.claon.center.common.annotation.RequestUser;
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
            @RequestUser String userId,
            @PathVariable String centerId,
            @RequestParam(value = "holdId", required = false) Optional<String> holdId,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return centerService.getCenterPosts(userId, centerId, holdId, pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterResponseDto create(
            @RequestUser String userId,
            @RequestBody @Valid CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userId, centerCreateRequestDto);
    }

    @GetMapping(value = "/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CenterDetailResponseDto findCenter(
            @RequestUser String userId,
            @PathVariable String centerId
    ) {
        return this.centerService.findCenter(userId, centerId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPreviewResponseDto> getCenterList(
            @RequestUser String userId,
            @RequestParam("option") CenterSearchOption option,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.centerService.findCenterListByOption(userId, option, pageable);
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
            @RequestUser String userId,
            @PathVariable String centerId,
            @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        return this.centerReviewService.createReview(userId, centerId, reviewCreateRequestDto);
    }

    @PutMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto updateReview(
            @RequestUser String userId,
            @PathVariable String reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto updateRequestDto
    ) {
        return this.centerReviewService.updateReview(userId, reviewId, updateRequestDto);
    }

    @DeleteMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto deleteReview(
            @RequestUser String userId,
            @PathVariable String reviewId
    ) {
        return this.centerReviewService.deleteReview(userId, reviewId);
    }

    @GetMapping(value = "/{centerId}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewBundleFindResponseDto findReviewByCenter(
            @RequestUser String userId,
            @PathVariable String centerId,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        return this.centerReviewService.findReview(userId, centerId, pageable);
    }

    @PostMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterBookmarkResponseDto create(
            @RequestUser String userId,
            @PathVariable String centerId
    ) {
        return this.centerBookmarkService.create(userId, centerId);
    }

    @DeleteMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(
            @RequestUser String userId,
            @PathVariable String centerId
    ) {
        this.centerBookmarkService.delete(userId, centerId);
    }

    @PostMapping(value = "/{centerId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterReportResponseDto createReport(
            @RequestUser String userId,
            @PathVariable String centerId,
            @RequestBody @Valid CenterReportCreateRequestDto centerReportCreateRequestDto
    ) {
        return this.centerService.createReport(userId, centerId, centerReportCreateRequestDto);
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

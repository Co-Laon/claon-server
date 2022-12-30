package coLaon.ClaonBack.center.web;

import coLaon.ClaonBack.center.domain.enums.CenterSearchOption;
import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterHoldInfoResponseDto;
import coLaon.ClaonBack.center.dto.CenterNameResponseDto;
import coLaon.ClaonBack.center.dto.CenterPostThumbnailResponseDto;
import coLaon.ClaonBack.center.dto.CenterPreviewResponseDto;
import coLaon.ClaonBack.center.dto.CenterReportCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterReportResponseDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.ReviewBundleFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.service.CenterBookmarkService;
import coLaon.ClaonBack.center.service.CenterReviewService;
import coLaon.ClaonBack.center.service.CenterService;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.UserDetails;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId,
            @RequestParam(value = "holdId", required = false) Optional<String> holdId,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return centerService.getCenterPosts(userDetails.getUser(), centerId, holdId, pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterResponseDto create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userDetails.getUser(), centerCreateRequestDto);
    }

    @GetMapping(value = "/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CenterDetailResponseDto findCenter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId
    ) {
        return this.centerService.findCenter(userDetails.getUser(), centerId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPreviewResponseDto> getCenterList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("option") CenterSearchOption option,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.centerService.findCenterListByOption(userDetails.getUser(), option, pageable);
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
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId,
            @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        return this.centerReviewService.createReview(userDetails.getUser(), centerId, reviewCreateRequestDto);
    }

    @PutMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto updateRequestDto
    ) {
        return this.centerReviewService.updateReview(userDetails.getUser(), reviewId, updateRequestDto);
    }

    @DeleteMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reviewId
    ) {
        return this.centerReviewService.deleteReview(userDetails.getUser(), reviewId);
    }

    @GetMapping(value = "/{centerId}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewBundleFindResponseDto findReviewByCenter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        return this.centerReviewService.findReview(userDetails.getUser(), centerId, pageable);
    }

    @PostMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterBookmarkResponseDto create(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId
    ) {
        return this.centerBookmarkService.create(userDetails.getUser(), centerId);
    }

    @DeleteMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId
    ) {
        this.centerBookmarkService.delete(userDetails.getUser(), centerId);
    }

    @PostMapping(value = "/{centerId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterReportResponseDto createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String centerId,
            @RequestBody @Valid CenterReportCreateRequestDto centerReportCreateRequestDto
    ) {
        return this.centerService.createReport(userDetails.getUser(), centerId, centerReportCreateRequestDto);
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

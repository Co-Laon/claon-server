package coLaon.ClaonBack.center.web;

import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.CenterSearchResponseDto;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewListFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.service.CenterBookmarkService;
import coLaon.ClaonBack.center.service.CenterReviewService;
import coLaon.ClaonBack.center.service.CenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/centers")
public class CenterController {
    private final CenterService centerService;
    private final CenterReviewService centerReviewService;
    private final CenterBookmarkService centerBookmarkService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterResponseDto create(
            @AuthenticationPrincipal String userId,
            @RequestBody CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userId, centerCreateRequestDto);
    }

    @GetMapping(value = "/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CenterDetailResponseDto findCenter(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId
    ) {
        return this.centerService.findCenter(userId, centerId);
    }

    @GetMapping(value = "/name/{keyword}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CenterSearchResponseDto> searchCenter(
            @AuthenticationPrincipal String userId,
            @PathVariable String keyword
    ) {
        return this.centerService.searchCenter(userId, keyword);
    }

    @GetMapping(value = "/{centerId}/hold")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HoldInfoResponseDto> findHoldInfoByCenter(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId
    ) {
        return this.centerService.findHoldInfoByCenterId(userId, centerId);
    }

    @PostMapping("/{centerId}/review")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ReviewResponseDto createReview(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId,
            @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        return this.centerReviewService.createReview(userId, centerId, reviewCreateRequestDto);
    }

    @PutMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto updateReview(
            @AuthenticationPrincipal String userId,
            @PathVariable String reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto updateRequestDto
    ) {
        return this.centerReviewService.updateReview(userId, reviewId, updateRequestDto);
    }

    @DeleteMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto deleteReview(
            @AuthenticationPrincipal String userId,
            @PathVariable String reviewId
    ) {
        return this.centerReviewService.deleteReview(userId, reviewId);
    }

    @GetMapping(value = "/{centerId}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewListFindResponseDto findReviewByCenter(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        return this.centerReviewService.findReview(userId, centerId, pageable);
    }

    @PostMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterBookmarkResponseDto create(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId
    ) {
        return this.centerBookmarkService.create(userId, centerId);
    }

    @DeleteMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(
            @AuthenticationPrincipal String userId,
            @PathVariable String centerId
    ) {
        this.centerBookmarkService.delete(userId, centerId);
    }
}

package com.claon.center.service;

import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.*;
import com.claon.center.dto.*;
import com.claon.center.repository.CenterRepository;
import com.claon.center.repository.ReviewRepository;
import com.claon.center.repository.ReviewRepositorySupport;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.BadRequestException;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CenterReviewServiceTest {
    @Mock
    CenterRepository centerRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    ReviewRepositorySupport reviewRepositorySupport;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    CenterReviewService centerReviewService;

    private final RequestUserInfo USER_INFO = new RequestUserInfo("USER_ID");
    private final RequestUserInfo WRONG_USER_INFO = new RequestUserInfo("WRONG_USER_ID");
    private Center center;
    private CenterReview review;

    @BeforeEach
    void setUp() {
        center = Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        ReflectionTestUtils.setField(center, "id", "center id");

        review = CenterReview.of(5, "testContent", USER_INFO.id(), center);
        ReflectionTestUtils.setField(review, "id", "reviewId");
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for create center review")
    void successCreateReview() {
        try (MockedStatic<CenterReview> reviewMockedStatic = mockStatic(CenterReview.class)) {
            // given
            ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(5, "testContent");

            given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));

            reviewMockedStatic.when(() -> CenterReview.of(5, "testContent", USER_INFO.id(), center)).thenReturn(review);

            given(reviewRepository.save(review)).willReturn(review);

            // when
            var reviewResponseDto = centerReviewService.createReview(USER_INFO, center.getId(), reviewCreateRequestDto);

            // then
            assertThat(reviewResponseDto)
                    .isNotNull()
                    .extracting("reviewId", "content")
                    .contains(review.getId(), review.getContent());
        }
    }

    @Test
    @DisplayName("Failure case for create center review for existing own review in center")
    void failureCreateReview_alreadyExist() {
        ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(5, "testContent");

        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));
        given(reviewRepository.findByUserIdAndCenterId(USER_INFO.id(), center.getId())).willReturn(Optional.of(review));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> centerReviewService.createReview(USER_INFO, center.getId(), reviewCreateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_ALREADY_EXIST, "이미 작성된 리뷰가 있습니다.");
    }

    @Test
    @DisplayName("Success case for update review")
    void successUpdateReview() {
        // given
        ReviewUpdateRequestDto reviewUpdateRequestDto = new ReviewUpdateRequestDto(1, "updateContent");

        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(reviewRepository.save(review)).willReturn(review);

        // when
        var reviewResponseDto = centerReviewService.updateReview(USER_INFO, review.getId(), reviewUpdateRequestDto);

        // then
        assertThat(reviewResponseDto)
                .isNotNull()
                .extracting("content", "reviewId")
                .contains(reviewUpdateRequestDto.getContent(), review.getId());
    }

    @Test
    @DisplayName("Failure case for update review because update by other user")
    void failUpdateReview_Unauthorized() {
        // given
        ReviewUpdateRequestDto reviewUpdateRequestDto = new ReviewUpdateRequestDto(1, "updateContent");

        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> centerReviewService.updateReview(WRONG_USER_INFO, review.getId(), reviewUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for delete review")
    void successDeleteReview() {
        // given
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        centerReviewService.deleteReview(USER_INFO, review.getId());

        // then
        assertThat(reviewRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Failure case for delete review because update by other user")
    void failDeleteReview_Unauthorized() {
        // given
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> centerReviewService.deleteReview(WRONG_USER_INFO, review.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for find center review")
    void successFindReview() {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        Page<CenterReview> centerReviewPage = new PageImpl<>(List.of(review), pageable, 2);

        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));
        given(reviewRepository.findByUserIdAndCenterId(USER_INFO.id(), center.getId())).willReturn(Optional.of(review));
        given(reviewRepositorySupport.findByCenterExceptBlockUserAndSelf(center.getId(), USER_INFO.id(), pageable)).willReturn(centerReviewPage);

        //when
        var reviewBundleFindResponseDto = centerReviewService.findReview(USER_INFO, center.getId(), pageable);

        // then
        assertThat(reviewBundleFindResponseDto.getOtherReviewsPagination().getResults())
                .isNotNull()
                .extracting(ReviewFindResponseDto::getReviewId, ReviewFindResponseDto::getRank)
                .contains(
                        tuple(review.getId(), review.getRank())
                );
    }
}

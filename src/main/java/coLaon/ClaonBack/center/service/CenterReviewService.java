package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterReview;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewListFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.ReviewRepository;
import coLaon.ClaonBack.center.repository.ReviewRepositorySupport;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CenterReviewService {
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRepositorySupport reviewRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Transactional
    public ReviewResponseDto createReview(
            String userId,
            String centerId,
            ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        this.reviewRepository.findByUserIdAndCenterId(writer.getId(), center.getId()).ifPresent(
                review -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 작성된 리뷰가 존재합니다."
                    );
                }
        );

        List<Integer> ranks = reviewRepository.selectRanksByCenterId(centerId);
        center.addRank(ranks, reviewCreateRequestDto.getRank(), ranks.size());

        return ReviewResponseDto.from(
                reviewRepository.save(
                        CenterReview.of(
                                reviewCreateRequestDto.getRank(),
                                reviewCreateRequestDto.getContent(),
                                writer,
                                center
                        )
                )
        );
    }

    @Transactional
    public ReviewResponseDto updateReview(
            String userId,
            String reviewId,
            ReviewUpdateRequestDto updateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), writer.getId()).validate();

        List<Integer> ranks = reviewRepository.selectRanksByCenterId(review.getCenter().getId());
        review.getCenter().changeRank(ranks, review.getRank(), updateRequestDto.getRank(), ranks.size());

        review.update(updateRequestDto.getRank(), updateRequestDto.getContent());

        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDto deleteReview(
            String userId,
            String reviewId
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        CenterReview review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "리뷰 정보를 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(review.getWriter().getId(), writer.getId()).validate();

        List<Integer> ranks = reviewRepository.selectRanksByCenterId(review.getCenter().getId());
        review.getCenter().deleteRank(ranks, review.getRank(), ranks.size());

        reviewRepository.delete(review);

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewListFindResponseDto findReview(String userId, String centerId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        List<Integer> ranks = reviewRepository.selectRanksByCenterId(centerId);
        center.updateRank((float) ranks.stream().mapToInt(r -> r).average().orElse(0));

        return ReviewListFindResponseDto.from(
                this.paginationFactory.create(
                        reviewRepositorySupport.findByCenterExceptBlockUser(center.getId(), userId, pageable)
                                .map(ReviewFindResponseDto::from)
                ),
                center
        );
    }
}

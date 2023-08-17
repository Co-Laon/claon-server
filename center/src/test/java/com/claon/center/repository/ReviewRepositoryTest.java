package com.claon.center.repository;

import com.claon.center.domain.*;
import com.claon.center.config.QueryDslTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslTestConfig.class, ReviewRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReviewRepositoryTest {
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewRepositorySupport reviewRepositorySupport;

    private final String USER_ID = "USER_ID";
    private Center center;

    @BeforeEach
    void setUp() {
        this.center = centerRepository.save(Center.of(
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
        ));

        reviewRepository.save(CenterReview.of(
                2,
                "test",
                USER_ID,
                this.center
        ));
    }

    @Test
    public void successFindByUserIdAndCenterId() {
        // given
        String centerId = this.center.getId();

        // when
        Optional<CenterReview> centerReviewOptional = reviewRepository.findByUserIdAndCenterId(USER_ID, centerId);

        // then
        assertThat(centerReviewOptional).isPresent();
    }

//    @Test
//    public void successFindByCenterExceptBlockUser() {
//        // given
//        reviewRepository.save(CenterReview.of(
//                2,
//                "test",
//                this.blockUser,
//                this.center
//        ));
//        String centerId = center.getId();
//        String userId = user.getId();
//
//        // when
//        Page<CenterReview> reviewList = reviewRepositorySupport.findByCenterExceptBlockUser(centerId, userId, PageRequest.of(0, 2));
//
//        // then
//        assertThat(reviewList.getContent().size()).isEqualTo(1);
//    }

//    @Test
//    public void successCountByCenterExceptBlockUser() {
//        // given
//        reviewRepository.save(CenterReview.of(
//                2,
//                "test",
//                this.blockUser,
//                this.center
//        ));
//        String centerId = center.getId();
//        String userId = user.getId();
//
//        // when
//        Integer countCenter = reviewRepositorySupport.countByCenterExceptBlockUser(centerId, userId);
//
//        // then
//        assertThat(countCenter).isEqualTo(1);
//    }

//    @Test
//    public void successFindByCenterExceptBlockUserAndSelf() {
//        // given
//        reviewRepository.save(CenterReview.of(
//                2,
//                "test",
//                this.blockUser,
//                this.center
//        ));
//        String centerId = center.getId();
//        String userId = user.getId();
//
//        // when
//        Page<CenterReview> reviewList = reviewRepositorySupport.findByCenterExceptBlockUserAndSelf(centerId, userId, PageRequest.of(0, 2));
//
//        // then
//        assertThat(reviewList.getContent().size()).isEqualTo(0);
//    }
}

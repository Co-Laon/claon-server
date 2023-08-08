package com.claon.repository;

import com.claon.center.domain.CenterImg;
import com.claon.center.domain.CenterReview;
import com.claon.center.domain.Charge;
import com.claon.center.domain.ChargeElement;
import com.claon.center.domain.OperatingTime;
import com.claon.center.domain.SectorInfo;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.CenterPreviewResponseDto;
import com.claon.center.repository.CenterBookmarkRepository;
import com.claon.center.repository.CenterRepositorySupport;
import com.claon.center.repository.ReviewRepository;
import com.claon.center.repository.SectorInfoRepository;
import com.claon.config.QueryDslTestConfig;
import com.claon.user.domain.User;
import com.claon.user.repository.UserRepository;
import com.claon.center.domain.Center;
import com.claon.center.domain.CenterBookmark;
import com.claon.center.repository.CenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslTestConfig.class, CenterRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CenterRepositoryTest {
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private CenterRepositorySupport centerRepositorySupport;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CenterBookmarkRepository centerBookmarkRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SectorInfoRepository sectorInfoRepository;

    private User user;

    private Center center;

    @BeforeEach
    void setUp() {
        this.user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        Center center = centerRepository.save(Center.of(
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

        this.center = centerRepository.save(Center.of(
                "center name",
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

        this.centerBookmarkRepository.save(CenterBookmark.of(center, this.user));

        this.reviewRepository.save(CenterReview.of(5, "test", this.user, center));

        this.sectorInfoRepository.save(SectorInfo.of(
                "testSectorInfo",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(1),
                center)
        );

        this.sectorInfoRepository.save(SectorInfo.of(
                "testSectorInfo2",
                "",
                "",
                center
        ));
    }

    @Test
    public void successSearchCenterName() {
        // given
        String keyword = "tes";

        // when
        List<Center> centerList = centerRepository.searchCenter(keyword);

        // then
        assertThat(centerList.size()).isEqualTo(1);
    }

    @Test
    public void successSearchCenter() {
        // given
        String name = "tes";

        // when
        Page<CenterPreviewResponseDto> centerPage = centerRepositorySupport.searchCenter(name, PageRequest.of(0, 2));

        // then
        assertThat(centerPage.getContent().get(0).getName()).isEqualTo("test");
    }

    @Test
    public void successFindCenterByOption() {
        // given
        String userId = this.user.getId();

        // when
        Page<CenterPreviewResponseDto> results = centerRepositorySupport.findCenterByOption(userId, CenterSearchOption.BOOKMARK, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);

        // when
        results = centerRepositorySupport.findCenterByOption(userId, CenterSearchOption.NEWLY_REGISTERED, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(2);

        // when
        results = centerRepositorySupport.findCenterByOption(userId, CenterSearchOption.NEW_SETTING, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successDeleteCenter() {
        // given
        centerRepository.deleteById(center.getId());
        centerRepository.flush();
        // when & then
        Optional<Center> resultCenter = centerRepository.findById(this.center.getId());
        Optional<CenterBookmark> resultCenterBookmark = centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), this.center.getId());
        assertThat(resultCenter.isPresent()).isFalse();
        assertThat(resultCenterBookmark.isPresent()).isFalse();
    }
}

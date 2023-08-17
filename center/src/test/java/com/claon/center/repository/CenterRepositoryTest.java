package com.claon.center.repository;

import com.claon.center.domain.*;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.CenterPreviewResponseDto;
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
    private CenterBookmarkRepository centerBookmarkRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SectorInfoRepository sectorInfoRepository;

    private final String USER_ID = "USER_ID";
    private Center center;

    @BeforeEach
    void setUp() {
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

        this.centerBookmarkRepository.save(CenterBookmark.of(center, USER_ID));

        this.reviewRepository.save(CenterReview.of(5, "test", USER_ID, center));

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
        // when
        var results = centerRepositorySupport.findCenterByOption(USER_ID, CenterSearchOption.BOOKMARK, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);

        // when
        results = centerRepositorySupport.findCenterByOption(USER_ID, CenterSearchOption.NEWLY_REGISTERED, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(2);

        // when
        results = centerRepositorySupport.findCenterByOption(USER_ID, CenterSearchOption.NEW_SETTING, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successDeleteCenter() {
        // given
        centerRepository.deleteById(center.getId());
        centerRepository.flush();

        // when
        Optional<Center> resultCenter = centerRepository.findById(this.center.getId());
        Optional<CenterBookmark> resultCenterBookmark = centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, this.center.getId());

        // then
        assertThat(resultCenter.isPresent()).isFalse();
        assertThat(resultCenterBookmark.isPresent()).isFalse();
    }
}

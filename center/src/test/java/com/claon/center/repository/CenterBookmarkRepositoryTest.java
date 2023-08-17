package com.claon.center.repository;

import com.claon.center.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CenterBookmarkRepositoryTest {
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private CenterBookmarkRepository centerBookmarkRepository;

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

        this.centerBookmarkRepository.save(CenterBookmark.of(this.center, USER_ID));
    }

    @Test
    public void successFindByUserIdAndCenterId() {
        // given
        String centerId = this.center.getId();

        // when
        Optional<CenterBookmark> centerBookmark = centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, centerId);

        // then
        assertThat(centerBookmark).isPresent();
    }
}

package com.claon.repository;

import com.claon.center.domain.CenterImg;
import com.claon.center.domain.Charge;
import com.claon.center.domain.ChargeElement;
import com.claon.center.domain.OperatingTime;
import com.claon.center.repository.CenterBookmarkRepository;
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
    private UserRepository userRepository;
    @Autowired
    private CenterBookmarkRepository centerBookmarkRepository;

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

        this.centerBookmarkRepository.save(CenterBookmark.of(this.center, this.user));
    }

    @Test
    public void successFindByUserIdAndCenterId() {
        // given
        String userId = this.user.getId();
        String centerId = this.center.getId();

        // when
        Optional<CenterBookmark> centerBookmark = centerBookmarkRepository.findByUserIdAndCenterId(userId, centerId);

        // then
        assertThat(centerBookmark).isPresent();
    }
}

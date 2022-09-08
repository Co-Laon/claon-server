package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class LaonRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LaonRepository laonRepository;

    private User user, laonUser;

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

        this.laonUser = userRepository.save(User.of(
                "laon@gmail.com",
                "1234567890",
                "laon",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        laonRepository.save(Laon.of(
                this.user,
                this.laonUser
        ));
    }

    @Test
    public void successFindByLaonIdAndUserId() {
        // given
        String userId = user.getId();
        String laonUserId = laonUser.getId();

        // when
        Optional<Laon> laonOptional = laonRepository.findByLaonIdAndUserId(laonUserId, userId);

        // then
        assertThat(laonOptional).isPresent();
    }

    @Test
    public void successFindAllByUserId() {
        // given
        String userId = user.getId();

        // when
        Page<Laon> laonList = laonRepository.findAllByUserId(userId, PageRequest.of(0, 2));

        // then
        assertThat(laonList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successGetUserIdsByLaonId() {
        // given
        String userId = laonUser.getId();

        // when
        List<String> userIdList = laonRepository.getUserIdsByLaonId(userId);

        // then
        assertThat(userIdList.size()).isEqualTo(1);
    }
}

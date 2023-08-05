package claon.repository;

import claon.config.QueryDslTestConfig;
import claon.user.domain.User;
import claon.user.repository.BlockUserRepository;
import claon.user.repository.LaonRepository;
import claon.user.repository.LaonRepositorySupport;
import claon.user.repository.UserRepository;
import claon.user.domain.BlockUser;
import claon.user.domain.Laon;
import claon.user.dto.LaonFindResponseDto;
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

@Import({QueryDslTestConfig.class, LaonRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class LaonRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LaonRepository laonRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;
    @Autowired
    private LaonRepositorySupport laonRepositorySupport;

    private User user, laonUser, blockUser;

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

        this.blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "1234567890",
                "block",
                175.0F,
                178.0F,
                "",
                "",
                "instagramBlockId"
        ));

        laonRepository.save(Laon.of(
                this.user,
                this.laonUser
        ));

        laonRepository.save(Laon.of(
                this.user,
                this.blockUser
        ));

        blockUserRepository.save(BlockUser.of(blockUser, user));
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
        Page<LaonFindResponseDto> laonList = laonRepositorySupport.findAllByUserId(userId, PageRequest.of(0, 2));

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

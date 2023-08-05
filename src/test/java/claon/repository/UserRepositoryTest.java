package claon.repository;

import claon.config.QueryDslTestConfig;
import claon.user.domain.User;
import claon.user.repository.BlockUserRepository;
import claon.user.repository.LaonRepository;
import claon.user.repository.UserRepository;
import claon.user.repository.UserRepositorySupport;
import claon.user.domain.BlockUser;
import claon.user.domain.Laon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslTestConfig.class, UserRepositorySupport.class})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRepositorySupport userRepositorySupport;
    @Autowired
    private LaonRepository laonRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "instagramId"
        ));

        User searchUser = userRepository.save(User.of(
                "search@gmail.com",
                "1234567777",
                "search",
                175.0F,
                178.0F,
                "",
                "123477",
                "instagramId"
        ));

        User blockedUser = userRepository.save(User.of(
                "block@gmail.com",
                "1234564444",
                "sear",
                175.0F,
                178.0F,
                "",
                "123444",
                "instagramId"
        ));

        laonRepository.save(Laon.of(user, searchUser));

        blockUserRepository.save(BlockUser.of(user, blockedUser));
    }

    @Test
    public void successFindByNickname() {
        // given
        String nickname = "test";

        // when
        Optional<User> userOptional = userRepository.findByNickname(nickname);

        // then
        assertThat(userOptional.isPresent()).isEqualTo(true);
    }

    @Test
    public void successSearchUser() {
        // given
        String nickname = "se";

        // when
        Page<User> userPage = userRepositorySupport.searchUser(user.getId(), nickname, PageRequest.of(0, 2));

        // then
        assertThat(userPage.getContent()).hasSize(1);
        assertThat(userPage.getContent().get(0).getNickname()).isEqualTo("search");
    }

    @Test
    public void successFindByEmailAndOAuthId() {
        // given
        String email = "test@gmail.com";
        String oAuthId = "1234567890";

        // when
        Optional<User> userOptional = userRepository.findByEmailAndOAuthId(email, oAuthId);

        // then
        assertThat(userOptional.isPresent()).isEqualTo(true);
    }

    @Test
    public void successFindByInstagramOAuthId() {
        // given
        String instagramOAuthId = "123456";

        // when
        Optional<User> userOptional = userRepository.findByInstagramOAuthId(instagramOAuthId);

        // then
        assertThat(userOptional.isPresent()).isEqualTo(true);
    }
}

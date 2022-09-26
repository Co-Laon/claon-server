package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.config.QueryDslTestConfig;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.repository.UserRepositorySupport;
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
        assertThat(userPage.getContent().size()).isEqualTo(1);
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

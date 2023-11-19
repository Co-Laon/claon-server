package com.claon.user.repository;

import com.claon.user.config.QueryDslTestConfig;
import com.claon.user.domain.BlockUser;
import com.claon.user.domain.Laon;
import com.claon.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    private User user, laonUser;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        ));

        laonUser = userRepository.save(User.of(
                "laon@gmail.com",
                "laon",
                175.0F,
                178.0F
        ));

        User blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "block",
                175.0F,
                178.0F
        ));

        laonRepository.save(Laon.of(
                user,
                laonUser
        ));

        laonRepository.save(Laon.of(
                user,
                blockUser
        ));

        blockUserRepository.save(BlockUser.of(blockUser, user));
    }

    @Test
    public void successFindByLaonIdAndUserId() {
        // given
        String userId = user.getId();
        String laonUserId = laonUser.getId();

        // when
        var laonOptional = laonRepository.findByLaonIdAndUserId(laonUserId, userId);

        // then
        assertThat(laonOptional).isPresent();
    }

    @Test
    public void successFindAllByUserId() {
        // given
        String userId = user.getId();

        // when
        var laonList = laonRepositorySupport.findAllByUserId(userId, PageRequest.of(0, 2));

        // then
        assertThat(laonList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successGetUserIdsByLaonId() {
        // given
        String userId = laonUser.getId();

        // when
        var userIdList = laonRepository.findUserIdsByLaonId(userId);

        // then
        assertThat(userIdList.size()).isEqualTo(1);
    }
}

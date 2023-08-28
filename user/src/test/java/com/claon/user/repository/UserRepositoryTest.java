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

    private User user, searchUser;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        ));

        searchUser = userRepository.save(User.of(
                "search@gmail.com",
                "search",
                175.0F,
                178.0F
        ));

        User blockedUser = userRepository.save(User.of(
                "block@gmail.com",
                "sear",
                175.0F,
                178.0F
        ));

        laonRepository.save(Laon.of(user, searchUser));

        blockUserRepository.save(BlockUser.of(user, blockedUser));
    }

    @Test
    public void successFindByNickname() {
        // given
        String nickname = "test";

        // when
        var result = userRepository.findByNickname(nickname);

        // then
        assertThat(result.isPresent()).isEqualTo(true);
    }

    @Test
    public void successSearchUser() {
        // given
        String nickname = "se";

        // when
        var result = userRepositorySupport.searchUser(user.getId(), nickname, PageRequest.of(0, 2));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNickname()).isEqualTo(searchUser.getNickname());
    }
}

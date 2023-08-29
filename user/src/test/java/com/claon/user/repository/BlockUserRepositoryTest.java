package com.claon.user.repository;

import com.claon.user.domain.BlockUser;
import com.claon.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BlockUserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;

    private User user, blockUser;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        ));

        blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "testBlockNickname",
                175.0F,
                178.0F
        ));

        blockUserRepository.save(BlockUser.of(
                user,
                blockUser
        ));
        blockUserRepository.save(BlockUser.of(
                blockUser,
                user
        ));
    }

    @Test
    public void successFindByUserIdAndBlockId() {
        // given
        String userId = user.getId();
        String blockUserId = blockUser.getId();

        // when
        var blockUserOptional = blockUserRepository.findByUserIdAndBlockId(userId, blockUserId);

        // then
        assertThat(blockUserOptional).isPresent();
    }

    @Test
    public void successFindByUserId() {
        // when
        var blockUserList = blockUserRepository.findByUser(user, PageRequest.of(0, 2));

        // then
        assertThat(blockUserList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindBlock() {
        // given
        String userId = user.getId();
        String blockUserId = blockUser.getId();

        // when
        var blockUserList = blockUserRepository.findBlock(userId, blockUserId);

        // then
        assertThat(blockUserList.size()).isEqualTo(2);
    }
}

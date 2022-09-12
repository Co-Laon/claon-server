package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
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
public class BlockUserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;

    private User user, blockUser;

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

        this.blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "1264567890",
                "testBlockNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        ));

        blockUserRepository.save(BlockUser.of(
                this.user,
                this.blockUser
        ));
        blockUserRepository.save(BlockUser.of(
                this.blockUser,
                this.user
        ));
    }

    @Test
    public void successFindByUserIdAndBlockId() {
        // given
        String userId = user.getId();
        String blockUserId = blockUser.getId();

        // when
        Optional<BlockUser> blockUserOptional = blockUserRepository.findByUserIdAndBlockId(userId, blockUserId);

        // then
        assertThat(blockUserOptional).isPresent();
    }

    @Test
    public void successFindByUserId() {
        // given
        String userId = user.getId();

        // when
        Page<BlockUser> blockUserList = blockUserRepository.findByUserId(userId, PageRequest.of(0, 2));

        // then
        assertThat(blockUserList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindBlock() {
        // given
        String userId = user.getId();
        String blockUserId = blockUser.getId();

        // when
        List<BlockUser> blockUserList = blockUserRepository.findBlock(userId, blockUserId);

        // then
        assertThat(blockUserList.size()).isEqualTo(2);
    }
}

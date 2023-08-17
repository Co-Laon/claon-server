package com.claon.gateway.repository;

import com.claon.gateway.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "instagramId"
        ));
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

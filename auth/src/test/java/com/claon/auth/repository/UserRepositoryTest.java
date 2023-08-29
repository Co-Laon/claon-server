package com.claon.auth.repository;

import com.claon.auth.domain.User;
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
        userRepository.save(User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
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
    public void successFindByEmail() {
        // given
        String email = "test@gmail.com";

        // when
        Optional<User> userOptional = userRepository.findByEmail(email);

        // then
        assertThat(userOptional.isPresent()).isEqualTo(true);
    }
}

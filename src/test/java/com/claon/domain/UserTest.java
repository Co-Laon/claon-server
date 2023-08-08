package com.claon.domain;

import com.claon.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "abcd",
                "hoonki",
                "hoonki",
                175.0F,
                178.0F,
                "sdd",
                "sdf",
                "dfdf"
        );
    }

    @Test
    @DisplayName("Success case for modify user")
    void successModifyUser() {
        // when
        this.user.modifyUser(
                "newnickname",
                175.0F,
                178.0F,
                "imagepath",
                "dfdf",
                "dsfsf"
        );

        // then
        assertThat(this.user)
                .extracting("nickname", "height", "armReach", "imagePath",
                        "instagramUserName", "instagramOAuthId")
                .contains("newnickname", 175.0F, 178.0F, "imagepath", "dsfsf", "dfdf");
    }

    @Test
    @DisplayName("Success case for check isCompletedSignUp")
    void checkIsCompletedSignUp() {
        // given # not completed user
        User user = User.createNewUser("cbh1203@naver.com", "2344");

        // when
        Boolean isCompleted = user.isSignupCompleted();

        // then
        assertThat(isCompleted).isFalse();

        // given # completed user
        User user2 = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );

        // when
        isCompleted = user2.isSignupCompleted();

        // then
        assertThat(isCompleted).isTrue();
    }
}

package com.claon.user.service;

import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.Laon;
import com.claon.user.domain.User;
import com.claon.user.dto.LaonResponseDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.LaonRepositorySupport;
import com.claon.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class LaonServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonRepository laonRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Mock
    LaonRepositorySupport laonRepositorySupport;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    LaonService laonService;

    private RequestUserInfo USER_INFO;
    private User user, laon;
    private Laon laonRelation;

    @BeforeEach
    void setUp() {
        laon = User.of(
                "test@gmail.com",
                "laonNickname",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(laon, "id", "laonId");

        user = User.of(
                "test@gmail.com",
                "userNickname",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(user, "id", "userId");

        laonRelation = Laon.of(
                user,
                laon
        );

        USER_INFO = new RequestUserInfo(user.getId());
    }

    @Test
    @DisplayName("Success case for create laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
            given(userRepository.findById(laon.getId())).willReturn(Optional.of(laon));
            given(laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId())).willReturn(Optional.empty());
            given(blockUserRepository.findBlock(user.getId(), laon.getId())).willReturn(List.of());

            mockedLaon.when(() -> Laon.of(user, laon)).thenReturn(laonRelation);

            given(laonRepository.save(laonRelation)).willReturn(laonRelation);

            // when
            laonService.createLaon(USER_INFO, laon.getId());

            // then
            assertThat(laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Failure case for create laon when laon myself")
    void failCreateLaonMyself() {
        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> laonService.createLaon(USER_INFO, user.getId())
        );

        //then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("자신을 %s할 수 없습니다.", Laon.domain));
    }

    @Test
    @DisplayName("Success case for delete laon")
    void successDeleteLaon() {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(laon.getId())).willReturn(Optional.of(laon));
        given(laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId())).willReturn(Optional.of(laonRelation));

        // when
        laonService.deleteLaon(USER_INFO, laon.getId());

        // then
        assertThat(laonRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find laons")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<LaonResponseDto> laonPage = new PageImpl<>(List.of(
                new LaonResponseDto(laonRelation.getLaon().getNickname())
        ), pageable, 2);

        given(laonRepositorySupport.findAllByUserId(user.getId(), pageable)).willReturn(laonPage);

        // when
        var laonFindResponseDto = laonService.findAllLaon(USER_INFO, pageable);

        // then
        assertThat(laonFindResponseDto.getResults())
                .isNotNull()
                .extracting(LaonResponseDto::getLaonNickname)
                .containsExactly(
                        laon.getNickname()
                );
    }
}
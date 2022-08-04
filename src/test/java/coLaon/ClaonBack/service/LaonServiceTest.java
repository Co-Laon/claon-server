package coLaon.ClaonBack.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.LaonFindResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.LaonService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class LaonServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonRepository laonRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    LaonService laonService;

    private User laon;
    private User laon2;
    private User user;
    private Laon laonRelation;
    private Laon laonRelation2;

    @BeforeEach
    void setUp() {
        this.laon = User.of(
                "laonId",
                "test@gmail.com",
                "1234567890",
                "laonNickname1",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.laon2 = User.of(
                "laonId2",
                "test1@gmail.com",
                "12345678902",
                "laonNickname2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId3"
        );

        this.user = User.of(
                "userId",
                "test@gmail.com",
                "1234567222",
                "userNickname2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.laonRelation = Laon.of(
                this.laon,
                this.user
        );

        this.laonRelation2 = Laon.of(
                this.laon2,
                this.user
        );
    }

    @Test
    @DisplayName("Success case for create laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
            given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
            given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.empty());

            mockedLaon.when(() -> Laon.of(this.laon, this.user)).thenReturn(this.laonRelation);

            given(this.laonRepository.save(this.laonRelation)).willReturn(this.laonRelation);

            // when
            this.laonService.createLaon("userNickname1", "userId");

            // then
            assertThat(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Failure case for create laon when laon myself")
    void failCreateLaonMyself() {
        //given
        given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(user));

        //when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.laonService.createLaon("userNickname1", "userId")
        );

        //then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_ACCESSIBLE);
        assertThat(ex.getMessage()).isEqualTo(String.format("자기 자신은 %s이 불가능합니다.", Laon.domain));
    }

    @Test
    @DisplayName("Success case for delete laon")
    void successDeleteLaon() {
        // given
        given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.of(this.laonRelation));

        // when
        this.laonService.deleteLaon("userNickname1", "userId");

        // then
        assertThat(this.laonRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find laons")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));

        Page<Laon> laons = new PageImpl<>(List.of(laonRelation, laonRelation2), pageable, 2);

        given(this.laonRepository.findAllByUserId("userId", pageable)).willReturn(laons);

        // when
        Pagination<LaonFindResponseDto> laonFindResponseDto = this.laonService.findAllLaon("userId", pageable);

        // then
        assertThat(laonFindResponseDto.getResults())
                .isNotNull()
                .extracting(LaonFindResponseDto::getLaonNickname, LaonFindResponseDto::getLaonProfileImage)
                .containsExactly(
                        tuple(this.laon.getNickname(), this.laon.getImagePath()),
                        tuple(this.laon2.getNickname(), this.laon2.getImagePath())
                );
    }
}
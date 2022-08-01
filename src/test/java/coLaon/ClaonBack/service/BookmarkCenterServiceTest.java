package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.BookmarkCenter;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.BookmarkCenterResponseDto;
import coLaon.ClaonBack.center.repository.BookmarkCenterRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.service.BookmarkCenterService;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class BookmarkCenterServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    BookmarkCenterRepository bookmarkCenterRepository;

    @InjectMocks
    BookmarkCenterService bookmarkCenterService;

    private User user, user2;
    private Center center;
    private BookmarkCenter bookmarkCenter;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "userId",
                "coraon.dev@gmail.com",
                "1234567890",
                "userNickname1",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.user2 = User.of(
                "userId2",
                "test123@gmail.com",
                "1234567890",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.center = Center.of(
                "centerId",
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );

        this.bookmarkCenter = BookmarkCenter.of(
                "bookmarkCenterId",
                this.center,
                this.user
        );
    }

    @Test
    @DisplayName("Success case for create bookmarkCenter")
    void successCreateBookmarkCenter() {
        try (MockedStatic<BookmarkCenter> mockedBookmarkCenter = mockStatic(BookmarkCenter.class)) {
            // given
            given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));
            given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
            given(this.bookmarkCenterRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.empty());

            mockedBookmarkCenter.when(() -> BookmarkCenter.of(this.center, this.user)).thenReturn(this.bookmarkCenter);

            given(this.bookmarkCenterRepository.save(this.bookmarkCenter)).willReturn(this.bookmarkCenter);

            // when
            BookmarkCenterResponseDto responseDto = this.bookmarkCenterService.create(this.user.getId(), this.center.getId());

            // then
            assertThat(responseDto)
                    .isNotNull()
                    .extracting("centerId", "isBookmarked")
                    .contains("centerId", true);
        }
    }

    @Test
    @DisplayName("Failure case for create bookmarkCenter")
    void failureCreateBookmarkCenter_already_exist() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.bookmarkCenterRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.of(bookmarkCenter));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> this.bookmarkCenterService.create(this.user.getId(), this.center.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_ALREADY_EXIST, "이미 즐겨찾기에 등록된 암장입니다.");
    }

    @Test
    @DisplayName("Success case for delete bookmarkCenter")
    void successDeleteBookmarkCenter() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.bookmarkCenterRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.of(this.bookmarkCenter));

        // when
        BookmarkCenterResponseDto responseDto = this.bookmarkCenterService.delete(this.user.getId(), this.center.getId());

        // then
        assertThat(this.bookmarkCenterRepository.findAll()).isEmpty();
        assertThat(responseDto)
                .extracting("centerId", "isBookmarked")
                .contains("centerId", false);
    }

    @Test
    @DisplayName("Failure case for delete bookmarkCenter")
    void failureDeleteBookmarkCenter_row_does_not_exist() {
        // given
        given(this.userRepository.findById("userId2")).willReturn(Optional.of(this.user2));
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> this.bookmarkCenterService.delete(this.user2.getId(), this.center.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_DOES_NOT_EXIST, "즐겨찾기에 등록되지 않은 암장입니다.");
    }
}

package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterBookmark;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.service.CenterBookmarkService;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class CenterBookmarkServiceTest {
    @Mock
    CenterRepository centerRepository;
    @Mock
    CenterBookmarkRepository centerBookmarkRepository;

    @InjectMocks
    CenterBookmarkService centerBookmarkService;

    private User user, user2;
    private Center center;
    private CenterBookmark centerBookmark;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "coraon.dev@gmail.com",
                "1234567890",
                "userNickname1",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.user, "id", "userId");

        this.user2 = User.of(
                "test123@gmail.com",
                "1234567890",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user2, "id", "userId2");

        this.center = Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        ReflectionTestUtils.setField(this.center, "id", "centerId");

        this.centerBookmark = CenterBookmark.of(
                this.center,
                this.user
        );
        ReflectionTestUtils.setField(this.centerBookmark, "id", "bookmarkCenterId");
    }

    @Test
    @DisplayName("Success case for create bookmarkCenter")
    void successCreateBookmarkCenter() {
        try (MockedStatic<CenterBookmark> mockedBookmarkCenter = mockStatic(CenterBookmark.class)) {
            // given
            given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
            given(this.centerBookmarkRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.empty());

            mockedBookmarkCenter.when(() -> CenterBookmark.of(this.center, this.user)).thenReturn(this.centerBookmark);

            given(this.centerBookmarkRepository.save(this.centerBookmark)).willReturn(this.centerBookmark);

            // when
            CenterBookmarkResponseDto responseDto = this.centerBookmarkService.create(this.user, this.center.getId());

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
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.centerBookmarkRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.of(centerBookmark));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> this.centerBookmarkService.create(this.user, this.center.getId())
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
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.centerBookmarkRepository.findByUserIdAndCenterId(this.user.getId(), this.center.getId())).willReturn(Optional.of(this.centerBookmark));

        // when
        CenterBookmarkResponseDto responseDto = this.centerBookmarkService.delete(this.user, this.center.getId());

        // then
        assertThat(this.centerBookmarkRepository.findAll()).isEmpty();
        assertThat(responseDto)
                .extracting("centerId", "isBookmarked")
                .contains("centerId", false);
    }

    @Test
    @DisplayName("Failure case for delete bookmarkCenter")
    void failureDeleteBookmarkCenter_row_does_not_exist() {
        // given
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> this.centerBookmarkService.delete(this.user2, this.center.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_DOES_NOT_EXIST, "즐겨찾기에 등록되지 않은 암장입니다.");
    }
}

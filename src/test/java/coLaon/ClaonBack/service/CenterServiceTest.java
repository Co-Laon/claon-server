package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterBookmark;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterImgDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.CenterSearchResponseDto;
import coLaon.ClaonBack.center.dto.ChargeDto;
import coLaon.ClaonBack.center.dto.HoldInfoRequestDto;
import coLaon.ClaonBack.center.dto.OperatingTimeDto;
import coLaon.ClaonBack.center.dto.SectorInfoDto;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.center.repository.ReviewRepositorySupport;
import coLaon.ClaonBack.center.service.CenterService;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CenterServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    HoldInfoRepository holdInfoRepository;
    @Mock
    ReviewRepositorySupport reviewRepositorySupport;
    @Mock
    CenterBookmarkRepository centerBookmarkRepository;
    @Mock
    PostRepositorySupport postRepositorySupport;

    @InjectMocks
    CenterService centerService;

    private User admin, user;
    private Center center, center2;
    private HoldInfo holdInfo, holdInfo2;
    private CenterBookmark centerBookmark;

    @BeforeEach
    void setUp() {
        this.admin = User.of(
                "coraon.dev@gmail.com",
                "1234567890",
                "userNickname1",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.admin, "id", "adminId");

        this.user = User.of(
                "test@gmail.com",
                "1234567222",
                "userNickname2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user, "id", "userId");

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
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        ReflectionTestUtils.setField(this.center, "id", "center id");

        this.center2 = Center.of(
                "test2",
                "test2",
                "010-2345-1234",
                "https://test2.com",
                "https://instagram2.com/test",
                "https://youtube2.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        ReflectionTestUtils.setField(this.center2, "id", "center id2");

        this.holdInfo = HoldInfo.of("test hold", "hold img test", this.center);
        this.holdInfo2 = HoldInfo.of("test hold2", "hold img test2", this.center);
        this.centerBookmark = CenterBookmark.of(center, user);
        ReflectionTestUtils.setField(this.centerBookmark, "id", "bookMarkId");
    }

    @Test
    @DisplayName("Success case for create center")
    void successCreateCenter() {
        CenterImg centerImg = CenterImg.of("img test");
        OperatingTime operatingTime = OperatingTime.of("매일", "10:00", "23:00");
        Charge charge = Charge.of("자유 패키지", "330,000");
        SectorInfo sectorInfo = SectorInfo.of("test sector", "1/1", "1/2");

        try (
                MockedStatic<Center> mockedCenter = mockStatic(Center.class);
                MockedStatic<HoldInfo> mockedHoldInfo = mockStatic(HoldInfo.class);
                MockedStatic<CenterImg> mockedCenterImg = mockStatic(CenterImg.class);
                MockedStatic<OperatingTime> mockedOperatingTime = mockStatic(OperatingTime.class);
                MockedStatic<Charge> mockedCharge = mockStatic(Charge.class);
                MockedStatic<SectorInfo> mockedSectorInfo = mockStatic(SectorInfo.class)
        ) {
            // given
            CenterCreateRequestDto requestDto = new CenterCreateRequestDto(
                    "test",
                    "test",
                    "010-1234-1234",
                    "https://test.com",
                    "https://instagram.com/test",
                    "https://youtube.com/channel/test",
                    List.of(new CenterImgDto("img test")),
                    List.of(new OperatingTimeDto("매일", "10:00", "23:00")),
                    "facilities test",
                    List.of(new ChargeDto("자유 패키지", "330,000")),
                    "charge img test",
                    List.of(new HoldInfoRequestDto("test hold", "hold img test")),
                    "hold info img test",
                    List.of(new SectorInfoDto("test sector", "1/1", "1/2"))
            );

            mockedCenterImg.when(() -> CenterImg.of("img test")).thenReturn(centerImg);
            mockedOperatingTime.when(() -> OperatingTime.of("매일", "10:00", "23:00")).thenReturn(operatingTime);
            mockedCharge.when(() -> Charge.of("자유 패키지", "330,000")).thenReturn(charge);
            mockedSectorInfo.when(() -> SectorInfo.of("test sector", "1/1", "1/2")).thenReturn(sectorInfo);

            mockedCenter.when(() -> Center.of(
                    "test",
                    "test",
                    "010-1234-1234",
                    "https://test.com",
                    "https://instagram.com/test",
                    "https://youtube.com/channel/test",
                    List.of(centerImg),
                    List.of(operatingTime),
                    "facilities test",
                    List.of(charge),
                    "charge img test",
                    "hold info img test",
                    List.of(sectorInfo)
            )).thenReturn(this.center);

            mockedHoldInfo.when(() -> HoldInfo.of(
                    "test hold", "hold img test", this.center
            )).thenReturn(this.holdInfo);

            given(this.userRepository.findById("adminId")).willReturn(Optional.of(this.admin));
            given(this.centerRepository.save(this.center)).willReturn(this.center);

            given(this.holdInfoRepository.save(this.holdInfo)).willReturn(this.holdInfo);

            // when
            CenterResponseDto responseDto = this.centerService.create("adminId", requestDto);

            // then
            assertThat(responseDto)
                    .isNotNull()
                    .extracting("id", "name")
                    .contains("center id", "test");
        }
    }

    @Test
    @DisplayName("Failure case for create center because create by non-admin")
    void failureAuthCreateCenter() {
        // given
        CenterCreateRequestDto requestDto = new CenterCreateRequestDto();

        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.centerService.create("userId", requestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for find center")
    void successFindCenter() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.centerBookmarkRepository.findByUserIdAndCenterId("userId", "centerId")).willReturn(Optional.of(centerBookmark));
        given(this.postRepositorySupport.countByCenterExceptBlockUser("centerId", "userId")).willReturn(0);
        given(this.reviewRepositorySupport.countByCenterExceptBlockUser("centerId", "userId")).willReturn(2);
        given(this.holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo, holdInfo2));

        //when
        CenterDetailResponseDto centerResponseDto = centerService.findCenter("userId", "centerId");

        //then
        assertThat(centerResponseDto)
                .isNotNull()
                .extracting(CenterDetailResponseDto::getAddress, CenterDetailResponseDto::getHoldInfoImg)
                .containsExactly(center.getAddress(), center.getHoldInfoImg());
    }

    @Test
    @DisplayName("Success case for find HoldInfo by center")
    void successFindHoldInfoByCenter() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        given(this.centerRepository.findById("center id")).willReturn(Optional.of(this.center));
        given(this.holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo, holdInfo2));

        // when
        List<HoldInfoResponseDto> holdInfoResponseDto = this.centerService.findHoldInfoByCenterId("userId", "center id");

        // then
        assertThat(holdInfoResponseDto)
                .isNotNull()
                .extracting(HoldInfoResponseDto::getName, HoldInfoResponseDto::getImage)
                .containsExactly(
                        tuple(holdInfo.getName(), holdInfo.getImg()),
                        tuple(holdInfo2.getName(), holdInfo2.getImg()));
    }

    @Test
    @DisplayName("Success case for search Center by keyword")
    void successSearchCenterByKeyword() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        given(this.centerRepository.searchCenter("te")).willReturn(List.of(this.center, this.center2));

        // when
        List<CenterSearchResponseDto> centerSearchResponseDto = this.centerService.searchCenter("userId", "te");

        // then
        assertThat(centerSearchResponseDto)
                .isNotNull()
                .extracting(CenterSearchResponseDto::getName)
                .contains(this.center.getName(), this.center2.getName());
    }
}

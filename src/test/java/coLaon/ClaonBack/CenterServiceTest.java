package coLaon.ClaonBack;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.HoldInfoRequestDto;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.center.service.CenterService;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @InjectMocks
    CenterService centerService;

    private User admin;
    private User user;
    private Center center;
    private HoldInfo holdInfo;

    @BeforeEach
    void setUp() {
        this.admin = User.of(
                "adminId",
                "coraon.dev@gmail.com",
                "1234567890",
                "userNickname1",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
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

        this.center = Center.of(
                "center id",
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

        this.holdInfo = HoldInfo.of("test hold", "hold img test", this.center);
    }

    @Test
    @DisplayName("Success case for create center")
    void successCreateCenter() {
        // given
        CenterCreateRequestDto requestDto = new CenterCreateRequestDto(
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
                List.of(new HoldInfoRequestDto("test hold", "hold img test")),
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );

        MockedStatic<Center> mockedCenter = mockStatic(Center.class);
        MockedStatic<HoldInfo> mockedHoldInfo = mockStatic(HoldInfo.class);

        given(this.userRepository.findById("adminId")).willReturn(Optional.of(this.admin));

        mockedCenter.when(() -> Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                requestDto.getImgList(),
                requestDto.getOperatingTimeList(),
                "facilities test",
                requestDto.getChargeList(),
                "charge img test",
                "hold info img test",
                requestDto.getSectorInfoList()
        )).thenReturn(this.center);

        given(this.centerRepository.save(this.center)).willReturn(this.center);

        mockedHoldInfo.when(() -> HoldInfo.of(
                "test hold", "hold img test", this.center
        )).thenReturn(this.holdInfo);

        given(this.holdInfoRepository.save(this.holdInfo)).willReturn(this.holdInfo);

        // when
        CenterResponseDto responseDto = this.centerService.create("adminId", requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto)
                .extracting("id", "name")
                .contains("center id", "test");
    }

    @Test
    @DisplayName("Failure case for create center because create by non-admin")
    void failureAuthCreateCenter() {
        // given
        CenterCreateRequestDto requestDto = new CenterCreateRequestDto();

        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));

        // when
        assertThatThrownBy(() -> this.centerService.create("userId", requestDto))
                // then
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("접근 권한이 없습니다.");
    }
}

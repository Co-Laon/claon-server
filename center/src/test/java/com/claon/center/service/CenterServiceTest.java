package com.claon.center.service;

import com.claon.center.domain.*;
import com.claon.center.domain.enums.CenterReportType;
import com.claon.center.dto.*;
import com.claon.center.repository.*;
import com.claon.center.common.domain.Pagination;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import com.claon.center.common.exception.UnauthorizedException;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CenterServiceTest {
    @Mock
    CenterRepository centerRepository;
    @Mock
    HoldInfoRepository holdInfoRepository;
    @Mock
    SectorInfoRepository sectorInfoRepository;
    @Mock
    ReviewRepositorySupport reviewRepositorySupport;
    @Mock
    CenterBookmarkRepository centerBookmarkRepository;
    @Mock
    CenterReportRepository centerReportRepository;
    @Mock
    CenterRepositorySupport centerRepositorySupport;
    @Mock
    PostPort postPort;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    CenterService centerService;

    private final String ADMIN_ID = "ADMIN_ID";
    private final String USER_ID = "USER_ID";
    private Center center, center2;
    private HoldInfo holdInfo, holdInfo2;
    private SectorInfo sectorInfo;
    private CenterBookmark centerBookmark;

    @BeforeEach
    void setUp() {
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
                "hold info img test"
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
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        ReflectionTestUtils.setField(this.center2, "id", "center id2");

        this.holdInfo = HoldInfo.of("test hold", "hold img test", this.center);
        ReflectionTestUtils.setField(this.holdInfo, "id", "holdId1");

        this.holdInfo2 = HoldInfo.of("test hold2", "hold img test2", this.center);
        ReflectionTestUtils.setField(this.holdInfo2, "id", "holdId2");

        this.sectorInfo = SectorInfo.of("test sector", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), this.center);

        this.centerBookmark = CenterBookmark.of(center, USER_ID);
        ReflectionTestUtils.setField(this.centerBookmark, "id", "bookMarkId");
    }

    @Test
    @DisplayName("Success case for find post by center")
    void successFindPostByCenter() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
//        Page<Post> postPage = new PageImpl<>(List.of(post, post2), pageable, 2);

        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(center));

//        Pagination<CenterPostThumbnailResponseDto> postPagination = paginationFactory.create(
//                new PageImpl<>(
//                        List.of(
//                                CenterPostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()),
//                                CenterPostThumbnailResponseDto.from(post2.getId(), post2.getThumbnailUrl())
//                        ), pageable, 2)
//        );
//        given(this.postPort.findByCenterExceptBlockUser(center.getId(), user.getId(), pageable)).willReturn(postPagination);

        // when
        var postThumbnailResponseDtoPagination =
                this.centerService.getCenterPosts(USER_ID, "centerId", Optional.empty(), pageable);

        //then
        assertThat(postThumbnailResponseDtoPagination.getResults())
                .isNotNull();
//                .extracting(CenterPostThumbnailResponseDto::getPostId, CenterPostThumbnailResponseDto::getThumbnailUrl)
//                .contains(
//                        tuple("testPostId", post.getThumbnailUrl()),
//                        tuple("testPostId2", post2.getThumbnailUrl())
//                );
    }

    @Test
    @DisplayName("Success case for find post by center and hold")
    void successFindPostByCenterAndHold() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
//        Page<Post> postPage = new PageImpl<>(List.of(post, post2), pageable, 2);

        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(center));
        given(this.holdInfoRepository.findByIdAndCenter("holdId1", center)).willReturn(Optional.of(holdInfo));

//        Pagination<CenterPostThumbnailResponseDto> postPagination = paginationFactory.create(
//                new PageImpl<>(
//                        List.of(
//                                CenterPostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()),
//                                CenterPostThumbnailResponseDto.from(post2.getId(), post2.getThumbnailUrl())
//                        ), pageable, 2)
//        );
//        given(this.postPort.findByCenterAndHoldExceptBlockUser(center.getId(), "holdId1", user.getId(), pageable)).willReturn(postPagination);

        // when
        Pagination<CenterPostThumbnailResponseDto> postThumbnailResponseDtoPagination =
                this.centerService.getCenterPosts(USER_ID, "centerId", Optional.of("holdId1"), pageable);

        //then
        assertThat(postThumbnailResponseDtoPagination.getResults())
                .isNotNull();
//                .extracting(CenterPostThumbnailResponseDto::getPostId, CenterPostThumbnailResponseDto::getThumbnailUrl)
//                .contains(
//                        tuple("testPostId", post.getThumbnailUrl())
//                );
    }

    @Test
    @DisplayName("Failure case for find post by center and hold with invalid hold info")
    void failFindPostByCenterAndHold() {
        //given
        Pageable pageable = PageRequest.of(0, 2);

        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(center));

        // when
        final NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> this.centerService.getCenterPosts(USER_ID, "centerId", Optional.of("invalidHoldId"), pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.DATA_DOES_NOT_EXIST, "홀드를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("Success case for create center")
    void successCreateCenter() {
        CenterImg centerImg = CenterImg.of("img test");
        OperatingTime operatingTime = OperatingTime.of("매일", "10:00", "23:00");
        ChargeElement chargeElement = ChargeElement.of("자유 패키지", "330,000");
        Charge charge = Charge.of(List.of(chargeElement), "charge image");

        try (
                MockedStatic<Center> mockedCenter = mockStatic(Center.class);
                MockedStatic<HoldInfo> mockedHoldInfo = mockStatic(HoldInfo.class);
                MockedStatic<CenterImg> mockedCenterImg = mockStatic(CenterImg.class);
                MockedStatic<OperatingTime> mockedOperatingTime = mockStatic(OperatingTime.class);
                MockedStatic<Charge> mockedCharge = mockStatic(Charge.class);
                MockedStatic<ChargeElement> mockedChargeElement = mockStatic(ChargeElement.class);
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
                    List.of(new ChargeDto(List.of(new ChargeElementDto("자유 패키지", "330,000")), "charge image")),
                    List.of(new HoldInfoRequestDto("test hold", "hold img test")),
                    "hold info img test",
                    List.of(new SectorInfoRequestDto("test sector", "2022/1/1", "2022/1/2"))
            );

            mockedCenterImg.when(() -> CenterImg.of("img test")).thenReturn(centerImg);
            mockedOperatingTime.when(() -> OperatingTime.of("매일", "10:00", "23:00")).thenReturn(operatingTime);
            mockedChargeElement.when(() -> ChargeElement.of("자유 패키지", "330,000")).thenReturn(chargeElement);
            mockedCharge.when(() -> Charge.of(List.of(chargeElement), "charge image")).thenReturn(charge);

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
                    "hold info img test"
            )).thenReturn(this.center);

            mockedHoldInfo.when(() -> HoldInfo.of(
                    "test hold", "hold img test", this.center
            )).thenReturn(this.holdInfo);

            mockedSectorInfo.when(() -> SectorInfo.of(
                    "test sector", "2022/1/1", "2022/1/2", this.center
            )).thenReturn(this.sectorInfo);

            given(this.centerRepository.save(this.center)).willReturn(this.center);

            given(this.holdInfoRepository.save(this.holdInfo)).willReturn(this.holdInfo);
            given(this.sectorInfoRepository.save(this.sectorInfo)).willReturn(this.sectorInfo);

            // when
            CenterResponseDto responseDto = this.centerService.create(ADMIN_ID, requestDto);

            // then
            assertThat(responseDto)
                    .isNotNull()
                    .extracting("id", "name")
                    .contains("center id", "test");
        }
    }

//    @Test
//    @DisplayName("Failure case for create center because create by non-admin")
//    void failureAuthCreateCenter() {
//        // given
//        CenterCreateRequestDto requestDto = new CenterCreateRequestDto();
//
//        // when
//        final UnauthorizedException ex = Assertions.assertThrows(
//                UnauthorizedException.class,
//                () -> this.centerService.create(USER_ID, requestDto)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
//    }

    @Test
    @DisplayName("Success case for find center")
    void successFindCenter() {
        // given
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, "centerId")).willReturn(Optional.of(centerBookmark));
//        given(this.postPort.countByCenterExceptBlockUser("centerId", USER_ID)).willReturn(0);
        given(this.reviewRepositorySupport.countByCenterExceptBlockUser("centerId", USER_ID)).willReturn(2);
        given(this.holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo, holdInfo2));
        given(this.sectorInfoRepository.findAllByCenter(center)).willReturn(List.of(sectorInfo));

        //when
        CenterDetailResponseDto centerResponseDto = centerService.findCenter(USER_ID, "centerId");

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
        given(this.centerRepository.findById("center id")).willReturn(Optional.of(this.center));
        given(this.holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo, holdInfo2));

        // when
        List<CenterHoldInfoResponseDto> holdInfoResponseDto = this.centerService.findHoldInfoByCenterId("center id");

        // then
        assertThat(holdInfoResponseDto)
                .isNotNull()
                .extracting(CenterHoldInfoResponseDto::getName, CenterHoldInfoResponseDto::getImage)
                .containsExactly(
                        tuple(holdInfo.getName(), holdInfo.getImg()),
                        tuple(holdInfo2.getName(), holdInfo2.getImg()));
    }

    @Test
    @DisplayName("Success case for search Center name by keyword")
    void successSearchCenterNameByKeyword() {
        // given
        given(this.centerRepository.searchCenter("te")).willReturn(List.of(this.center, this.center2));

        // when
        List<CenterNameResponseDto> centerNameResponseDto = this.centerService.searchCenterName("te");

        // then
        assertThat(centerNameResponseDto)
                .isNotNull()
                .extracting(CenterNameResponseDto::getName)
                .contains(this.center.getName(), this.center2.getName());
    }

    @Test
    @DisplayName("Success case for search Center")
    void successSearchCenter() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<CenterPreviewResponseDto> centerPage = new PageImpl<>(List.of(new CenterPreviewResponseDto(center.getId(), center.getName(), center.getImgList(), 5.0)), pageable, 2);

        given(this.centerRepositorySupport.searchCenter(this.center.getName(), pageable)).willReturn(centerPage);

        // when
        Pagination<CenterPreviewResponseDto> centerPreviewResponseDtoPagination = this.centerService.searchCenter(this.center.getName(), pageable);

        // then
        assertThat(centerPreviewResponseDtoPagination.getResults())
                .isNotNull()
                .extracting(CenterPreviewResponseDto::getId, CenterPreviewResponseDto::getName)
                .contains(
                        tuple(center.getId(), center.getName())
                );
    }

    @Test
    @DisplayName("Success case for create center report")
    void successCreateReport() {
        CenterReport centerReport = CenterReport.of(
                "test",
                CenterReportType.TELEPHONE,
                USER_ID,
                this.center
        );
        ReflectionTestUtils.setField(centerReport, "id", "reportId");

        try (MockedStatic<CenterReport> mockedCenterReport = mockStatic(CenterReport.class)) {
            // given
            CenterReportCreateRequestDto centerReportCreateRequestDto = new CenterReportCreateRequestDto(
                    "test",
                    CenterReportType.TELEPHONE
            );

            given(this.centerRepository.findById("center id")).willReturn(Optional.of(this.center));

            mockedCenterReport.when(() -> CenterReport.of(
                    "test",
                    CenterReportType.TELEPHONE,
                    USER_ID,
                    this.center
            )).thenReturn(centerReport);
            given(this.centerReportRepository.save(centerReport)).willReturn(centerReport);

            // when
            CenterReportResponseDto centerReportResponseDto = this.centerService.createReport(USER_ID, "center id", centerReportCreateRequestDto);

            // then
            assertThat(centerReportResponseDto)
                    .isNotNull()
                    .extracting(CenterReportResponseDto::getId, CenterReportResponseDto::getContent)
                    .containsExactly(centerReport.getId(), centerReport.getContent());
        }
    }
}

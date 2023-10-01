package com.claon.center.service;

import com.claon.center.common.domain.RequestUserInfo;
import com.claon.center.domain.*;
import com.claon.center.domain.enums.CenterReportType;
import com.claon.center.dto.*;
import com.claon.center.dto.request.CenterRequestDto;
import com.claon.center.dto.request.CenterReportRequestDto;
import com.claon.center.dto.request.HoldInfoRequestDto;
import com.claon.center.dto.request.SectorInfoRequestDto;
import com.claon.center.repository.*;
import com.claon.center.common.domain.Pagination;
import com.claon.center.common.domain.PaginationFactory;
import com.claon.center.common.exception.ErrorCode;
import com.claon.center.common.exception.NotFoundException;
import com.claon.center.service.client.PostClient;
import com.claon.center.service.client.dto.PostThumbnailResponse;
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
    PostClient postClient;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    CenterService centerService;

    private final RequestUserInfo USER_INFO = new RequestUserInfo("USER_ID");
    private Center center;
    private HoldInfo holdInfo;
    private SectorInfo sectorInfo;
    private CenterBookmark centerBookmark;

    @BeforeEach
    void setUp() {
        center = Center.of(
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
        ReflectionTestUtils.setField(center, "id", "center id");

        holdInfo = HoldInfo.of("test hold", "hold img test", center);
        ReflectionTestUtils.setField(holdInfo, "id", "holdId");

        sectorInfo = SectorInfo.of("test sector", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), center);

        centerBookmark = CenterBookmark.of(center, USER_INFO.id());
        ReflectionTestUtils.setField(centerBookmark, "id", "bookMarkId");
    }

    @Test
    @DisplayName("Success case for find post by center")
    void successFindPostByCenter() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));

        Pagination<PostThumbnailResponse> postPagination = paginationFactory.create(
                new PageImpl<>(
                        List.of(
                                new PostThumbnailResponse("postId", "thumbnailUrl")
                        ), pageable, 1)
        );
        given(postClient.findPostThumbnails(USER_INFO.id(), center.getId(), Optional.empty(), pageable)).willReturn(postPagination);

        // when
        var postThumbnailResponseDtoPagination =
                centerService.getCenterPosts(USER_INFO, center.getId(), Optional.empty(), pageable);

        //then
        assertThat(postThumbnailResponseDtoPagination.getResults())
                .isNotNull()
                .extracting(PostThumbnailResponse::postId, PostThumbnailResponse::thumbnailUrl)
                .contains(
                        tuple("postId", "thumbnailUrl")
                );
    }

    @Test
    @DisplayName("Success case for find post by center and hold")
    void successFindPostByCenterAndHold() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));
        given(holdInfoRepository.findByIdAndCenter(holdInfo.getId(), center)).willReturn(Optional.of(holdInfo));

        Pagination<PostThumbnailResponse> postPagination = paginationFactory.create(
                new PageImpl<>(
                        List.of(
                                new PostThumbnailResponse("postId", "thumbnailUrl")
                        ), pageable, 1)
        );
        given(postClient.findPostThumbnails(USER_INFO.id(), center.getId(), Optional.of(holdInfo.getId()), pageable)).willReturn(postPagination);

        // when
        var postThumbnailResponseDtoPagination =
                centerService.getCenterPosts(USER_INFO, center.getId(), Optional.of(holdInfo.getId()), pageable);

        //then
        assertThat(postThumbnailResponseDtoPagination.getResults())
                .isNotNull()
                .extracting(PostThumbnailResponse::postId, PostThumbnailResponse::thumbnailUrl)
                .contains(
                        tuple("postId", "thumbnailUrl")
                );
    }

    @Test
    @DisplayName("Failure case for find post by center and hold with invalid hold info")
    void failFindPostByCenterAndHold() {
        //given
        Pageable pageable = PageRequest.of(0, 2);

        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));

        // when
        final NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> centerService.getCenterPosts(USER_INFO, center.getId(), Optional.of("wrongId"), pageable)
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
            CenterRequestDto requestDto = new CenterRequestDto(
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
            )).thenReturn(center);

            mockedHoldInfo.when(() -> HoldInfo.of(
                    "test hold", "hold img test", center
            )).thenReturn(holdInfo);

            mockedSectorInfo.when(() -> SectorInfo.of(
                    "test sector", "2022/1/1", "2022/1/2", center
            )).thenReturn(sectorInfo);

            given(centerRepository.save(center)).willReturn(center);

            given(holdInfoRepository.save(holdInfo)).willReturn(holdInfo);
            given(sectorInfoRepository.save(sectorInfo)).willReturn(sectorInfo);

            // when
            var responseDto = centerService.create(USER_INFO, requestDto);

            // then
            assertThat(responseDto)
                    .isNotNull()
                    .extracting("id", "name")
                    .contains(center.getId(), center.getName());
        }
    }

    @Test
    @DisplayName("Success case for find center")
    void successFindCenter() {
        // given
        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));
        given(centerBookmarkRepository.findByUserIdAndCenterId(USER_INFO.id(), center.getId())).willReturn(Optional.of(centerBookmark));
        given(postClient.countPostsByCenterId(USER_INFO.id(), center.getId())).willReturn(2L);
        given(reviewRepositorySupport.countByCenterExceptBlockUser(center.getId(), USER_INFO.id())).willReturn(2L);
        given(holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo));
        given(sectorInfoRepository.findAllByCenter(center)).willReturn(List.of(sectorInfo));

        //when
        var centerResponseDto = centerService.findCenter(USER_INFO, center.getId());

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
        given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));
        given(holdInfoRepository.findAllByCenter(center)).willReturn(List.of(holdInfo));

        // when
        var holdInfoResponseDto = centerService.findHoldInfoByCenterId(center.getId());

        // then
        assertThat(holdInfoResponseDto)
                .isNotNull()
                .extracting(HoldInfoResponseDto::getName, HoldInfoResponseDto::getImage)
                .containsExactly(
                        tuple(holdInfo.getName(), holdInfo.getImg())
                );
    }

    @Test
    @DisplayName("Success case for search Center name by keyword")
    void successSearchCenterNameByKeyword() {
        // given
        String keyword = "te";
        given(centerRepository.searchCenter(keyword)).willReturn(List.of(center));

        // when
        var centerNameResponseDto = centerService.searchCenterName(keyword);

        // then
        assertThat(centerNameResponseDto)
                .isNotNull()
                .extracting(CenterNameResponseDto::getName)
                .contains(center.getName());
    }

    @Test
    @DisplayName("Success case for search Center")
    void successSearchCenter() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<CenterPreviewResponseDto> centerPage = new PageImpl<>(
                List.of(new CenterPreviewResponseDto(center.getId(), center.getName(), center.getImgList(), 5.0)),
                pageable,
                2
        );

        given(centerRepositorySupport.searchCenter(center.getName(), pageable)).willReturn(centerPage);

        // when
        var centerPreviewResponseDtoPagination = centerService.searchCenter(center.getName(), pageable);

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
                USER_INFO.id(),
                center
        );
        ReflectionTestUtils.setField(centerReport, "id", "reportId");

        try (MockedStatic<CenterReport> mockedCenterReport = mockStatic(CenterReport.class)) {
            // given
            CenterReportRequestDto centerReportRequestDto = new CenterReportRequestDto(
                    "test",
                    CenterReportType.TELEPHONE
            );

            given(centerRepository.findById(center.getId())).willReturn(Optional.of(center));

            mockedCenterReport.when(() -> CenterReport.of(
                    "test",
                    CenterReportType.TELEPHONE,
                    USER_INFO.id(),
                    center
            )).thenReturn(centerReport);
            given(centerReportRepository.save(centerReport)).willReturn(centerReport);

            // when
            var centerReportResponseDto = centerService.createReport(USER_INFO, center.getId(), centerReportRequestDto);

            // then
            assertThat(centerReportResponseDto)
                    .isNotNull()
                    .extracting(CenterReportResponseDto::getId, CenterReportResponseDto::getContent)
                    .containsExactly(centerReport.getId(), centerReport.getContent());
        }
    }
}

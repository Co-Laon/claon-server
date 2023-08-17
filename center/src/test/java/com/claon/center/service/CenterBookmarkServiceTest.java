package com.claon.center.service;

import com.claon.center.domain.*;
import com.claon.center.dto.CenterBookmarkResponseDto;
import com.claon.center.repository.CenterBookmarkRepository;
import com.claon.center.repository.CenterRepository;
import com.claon.center.common.exception.BadRequestException;
import com.claon.center.common.exception.ErrorCode;
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

    private final String USER_ID = "USER_ID";
    private final String USER2_ID = "USER2_ID";
    private Center center;
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
        ReflectionTestUtils.setField(this.center, "id", "centerId");

        this.centerBookmark = CenterBookmark.of(
                this.center,
                USER_ID
        );
        ReflectionTestUtils.setField(this.centerBookmark, "id", "bookmarkCenterId");
    }

    @Test
    @DisplayName("Success case for create bookmarkCenter")
    void successCreateBookmarkCenter() {
        try (MockedStatic<CenterBookmark> mockedBookmarkCenter = mockStatic(CenterBookmark.class)) {
            // given
            given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
            given(this.centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, this.center.getId())).willReturn(Optional.empty());

            mockedBookmarkCenter.when(() -> CenterBookmark.of(this.center, USER_ID)).thenReturn(this.centerBookmark);

            given(this.centerBookmarkRepository.save(this.centerBookmark)).willReturn(this.centerBookmark);

            // when
            CenterBookmarkResponseDto responseDto = this.centerBookmarkService.create(USER_ID, this.center.getId());

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
        given(this.centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, this.center.getId())).willReturn(Optional.of(centerBookmark));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> this.centerBookmarkService.create(USER_ID, this.center.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_ALREADY_EXIST, "이미 즐겨찾기에 등록되어 있습니다.");
    }

    @Test
    @DisplayName("Success case for delete bookmarkCenter")
    void successDeleteBookmarkCenter() {
        // given
        given(this.centerRepository.findById("centerId")).willReturn(Optional.of(this.center));
        given(this.centerBookmarkRepository.findByUserIdAndCenterId(USER_ID, this.center.getId())).willReturn(Optional.of(this.centerBookmark));

        // when
        CenterBookmarkResponseDto responseDto = this.centerBookmarkService.delete(USER_ID, this.center.getId());

        // then
        org.assertj.core.api.Assertions.assertThat(this.centerBookmarkRepository.findAll()).isEmpty();
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
                () -> this.centerBookmarkService.delete(USER2_ID, this.center.getId())
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.ROW_DOES_NOT_EXIST, "아직 즐겨찾기에 등록되지 않았습니다.");
    }
}

package com.claon.post.service;

import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.domain.Notice;
import com.claon.post.dto.NoticeCreateRequestDto;
import com.claon.post.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    @Mock
    NoticeRepository noticeRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    NoticeService noticeService;

    private final String ADMIN_ID = "ADMIN_ID";
    private Notice notice;

    @BeforeEach
    void setUp() {
        notice = Notice.of("title", "content", ADMIN_ID);
        ReflectionTestUtils.setField(notice, "createdAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for get notice")
    void successGetNoticeList() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(noticeRepository.findAllWithPagination(pageable)).willReturn(new PageImpl<>(List.of(notice), pageable, 1));

        // when
        var pagination = noticeService.getNoticeList(pageable);

        // then
        assertThat(pagination.getResults().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Success case for create notice")
    void successCreateNotice() {
        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("title", "content");

        try (MockedStatic<Notice> mockedNotice = mockStatic(Notice.class)) {
            // given
            mockedNotice.when(() -> Notice.of(
                    dto.getTitle(),
                    dto.getContent(),
                    ADMIN_ID
            )).thenReturn(notice);

            given(noticeRepository.save(notice)).willReturn(notice);

            // when
            var result = noticeService.createNotice(ADMIN_ID, dto);

            // then
            assertThat(result)
                    .extracting("title", "content")
                    .contains(notice.getTitle(), notice.getContent());
        }
    }

//    @Test
//    @DisplayName("Fail case for create notice")
//    void failCreateNotice() {
//        // given
//        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("title", "content");
//
//        // when
//        final UnauthorizedException ex = Assertions.assertThrows(
//                UnauthorizedException.class,
//                () -> noticeService.createNotice(USER_ID, dto)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
//    }
}

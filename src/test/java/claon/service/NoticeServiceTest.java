package claon.service;

import claon.common.domain.Pagination;
import claon.common.domain.PaginationFactory;
import claon.common.exception.ErrorCode;
import claon.common.exception.UnauthorizedException;
import claon.user.domain.User;
import claon.notice.domain.Notice;
import claon.notice.dto.NoticeCreateRequestDto;
import claon.notice.dto.NoticeResponseDto;
import claon.notice.repository.NoticeRepository;
import claon.notice.service.NoticeService;

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

    private User adminUser, generalUser;

    @BeforeEach
    void setUp() {
        this.generalUser = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.generalUser, "id", "generalUserId");

        this.adminUser = User.of(
                "coraon.dev@gmail.com",
                "test2345!!",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.adminUser, "id", "adminUserId");
    }

    @Test
    @DisplayName("Success case for get notice")
    void successGetNoticeList() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Notice sampleNotice = Notice.of("asdf", "asdfaf", this.adminUser);
        ReflectionTestUtils.setField(sampleNotice, "createdAt", LocalDateTime.now());
        given(this.noticeRepository.findAllWithPagination(pageable)).willReturn(new PageImpl<>(List.of(sampleNotice), pageable, 1));

        // when
        Pagination<NoticeResponseDto> pagination = this.noticeService.getNoticeList(pageable);

        // then
        assertThat(pagination.getResults().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Success case for create notice")
    void successCreateNotice() {
        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("asdf", "ASdf");
        Notice notice = Notice.of(
                dto.getTitle(),
                dto.getContent(),
                this.adminUser
        );
        ReflectionTestUtils.setField(notice, "createdAt", LocalDateTime.now());

        try (MockedStatic<Notice> mockedNotice = mockStatic(Notice.class)) {
            // given
            mockedNotice.when(() -> Notice.of(
                    dto.getTitle(),
                    dto.getContent(),
                    this.adminUser
            )).thenReturn(notice);

            given(this.noticeRepository.save(notice)).willReturn(notice);

            // when
            NoticeResponseDto result = this.noticeService.createNotice(this.adminUser, dto);

            // then
            assertThat(result)
                    .extracting("title", "content")
                    .contains("asdf", "ASdf");
        }
    }

    @Test
    @DisplayName("Fail case for create notice")
    void failCreateNotice() {
        // given
        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("asdf", "ASdf");

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.noticeService.createNotice(this.generalUser, dto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}

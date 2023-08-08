package com.claon.repository;

import com.claon.user.domain.User;
import com.claon.user.repository.UserRepository;
import com.claon.notice.domain.Notice;
import com.claon.notice.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class NoticeRepositoryTest {
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private UserRepository userRepository;

    private User adminUser;

    @BeforeEach
    void setUp() {
        // given
        this.adminUser = this.userRepository.save(User.of(
                "coraon.dev@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        this.noticeRepository.save(Notice.of("abcd", "sfdasfd", adminUser));
    }

    @Test
    public void successFindAll() {
        // when
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Notice> results = noticeRepository.findAllWithPagination(pageable);

        // then
        assertThat(results.getTotalElements()).isEqualTo(1L);
    }
}

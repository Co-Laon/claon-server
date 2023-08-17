package com.claon.post.repository;

import com.claon.post.domain.Notice;
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

    private final String ADMIN_ID = "ADMIN_ID";

    @BeforeEach
    void setUp() {
        this.noticeRepository.save(Notice.of("abcd", "sfdasfd", ADMIN_ID));
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

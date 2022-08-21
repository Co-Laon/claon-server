package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.notice.domain.Notice;
import coLaon.ClaonBack.notice.repository.NoticeRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
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
    private Notice notice;


    @BeforeEach
    void setUp() {
        // given
        this.adminUser = this.userRepository.save(User.of(
                "coraon.dev@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        ));

        this.notice = this.noticeRepository.save(Notice.of("abcd", "sfdasfd", adminUser));
    }

    @Test
    public void successFindAll() {
        // when
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Notice> results = noticeRepository.findAllWithPagination(pageable);

        // then
        assertThat(results.getSize()).isEqualTo(1);
    }

    @Test
    public void successCreate(){
        Notice notice1 = Notice.of("asdf", "sfdasfd", adminUser);
        notice1 = noticeRepository.save(notice1);

        assertThat(notice1.getId()).isNotNull();
    }
}

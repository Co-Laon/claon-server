package com.claon.post.repository;

import com.claon.post.domain.Post;
import com.claon.post.domain.PostReport;
import com.claon.post.domain.enums.PostReportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostReportRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostReportRepository postReportRepository;

    private final String USER_ID = "USER_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post post;

    @BeforeEach
    void setUp() {
        this.post = postRepository.save(Post.of(
                CENTER_ID,
                "testContent1",
                USER_ID,
                List.of(),
                List.of()
        ));

        postReportRepository.save(PostReport.of(
                USER_ID, this.post, PostReportType.INAPPROPRIATE_POST, "testContent"
        ));
    }

    @Test
    public void successFindByReporterAndPost() {
        // when
        Optional<PostReport> postReport = postReportRepository.findByReporterIdAndPost(USER_ID, this.post);

        // then
        assertThat(postReport).isPresent();
    }
}

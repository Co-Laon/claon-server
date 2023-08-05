package claon.post.domain;

import claon.common.domain.BaseEntity;
import claon.post.domain.enums.PostReportType;
import claon.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_post_report")
@NoArgsConstructor
public class PostReport extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PostReportType postReportType;
    @Column(name = "content", length = 1000)
    private String content;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User reporter;
    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private PostReport(
            User reporter,
            Post post,
            PostReportType postReportType,
            String content
    ) {
        this.reporter = reporter;
        this.post = post;
        this.postReportType = postReportType;
        this.content = content;
    }

    public static PostReport of(
            User reporter,
            Post post,
            PostReportType postReportType,
            String content
    ) {
        return new PostReport(reporter, post, postReportType, content);
    }
}
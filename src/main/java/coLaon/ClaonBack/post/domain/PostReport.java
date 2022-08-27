package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.post.domain.enums.ReportType;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_post_report")
@NoArgsConstructor
public class PostReport extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User reporter;
    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    private ReportType reportType;
    private String content;

    private PostReport(User reporter, Post post, ReportType reportType, String content) {
        this.reporter = reporter;
        this.post = post;
        this.reportType = reportType;
        this.content = content;
    }

    public static PostReport of(User reporter, Post post, ReportType reportType, String content) {
        return new PostReport(reporter, post, reportType, content);
    }
}
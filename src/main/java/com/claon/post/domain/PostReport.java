package com.claon.post.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.post.domain.enums.PostReportType;
import com.claon.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
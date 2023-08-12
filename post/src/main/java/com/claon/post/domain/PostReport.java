package com.claon.post.domain;

import com.claon.post.common.domain.BaseEntity;
import com.claon.post.domain.enums.PostReportType;
import jakarta.persistence.*;
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

    @Column(name = "user_id")
    private String reporterId;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private PostReport(
            String reporterId,
            Post post,
            PostReportType postReportType,
            String content
    ) {
        this.reporterId = reporterId;
        this.post = post;
        this.postReportType = postReportType;
        this.content = content;
    }

    public static PostReport of(
            String reporterId,
            Post post,
            PostReportType postReportType,
            String content
    ) {
        return new PostReport(reporterId, post, postReportType, content);
    }
}
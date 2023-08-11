package com.claon.post.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import com.claon.center.domain.Center;
import com.claon.post.domain.converter.PostContentsConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Table(name = "tb_post")
@NoArgsConstructor
public class Post extends BaseEntity {
    @ManyToOne(targetEntity = Center.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Column(name = "content", length = 500)
    private String content;

    @Convert(converter = PostContentsConverter.class)
    @Column(name = "content_list", length = 2000)
    private List<PostContents> contentList;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClimbingHistory> climbingHistoryList;

    public String getThumbnailUrl() {
        if (this.getContentList().isEmpty()) {
            return null;
        }
        return this.getContentList().get(0).getUrl();
    }

    private Post(
            Center center,
            String content,
            List<PostContents> contentList,
            User writer
    ) {
        this.center = center;
        this.content = content;
        this.writer = writer;
        this.contentList = contentList;
        this.isDeleted = false;
    }

    private Post(
            Center center,
            String content,
            User writer,
            List<PostContents> contentList,
            List<ClimbingHistory> climbingHistoryList
    ) {
        this.center = center;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentList = contentList;
        this.climbingHistoryList = climbingHistoryList;
    }

    public static Post of(
            Center center,
            String content,
            List<PostContents> contentList,
            User writer
    ) {
        return new Post(
                center,
                content,
                contentList,
                writer
        );
    }

    public static Post of(
            Center center,
            String content,
            User writer,
            List<PostContents> contentsList,
            List<ClimbingHistory> climbingHistoryList
    ) {
        return new Post(
                center,
                content,
                writer,
                contentsList,
                climbingHistoryList
        );
    }

    public void update(
            String content,
            List<PostContents> contentList
    ) {
        this.content = content;
        this.contentList = contentList;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
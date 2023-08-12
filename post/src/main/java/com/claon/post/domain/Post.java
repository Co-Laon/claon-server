package com.claon.post.domain;

import com.claon.post.common.domain.BaseEntity;
import com.claon.post.domain.converter.PostContentsConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Getter
@Table(name = "tb_post")
@NoArgsConstructor
public class Post extends BaseEntity {
    @Column(name = "center_id", nullable = false)
    private String centerId;

    @Column(name = "content", length = 500)
    private String content;

    @Convert(converter = PostContentsConverter.class)
    @Column(name = "content_list", length = 2000)
    private List<PostContents> contentList;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "user_id", nullable = false)
    private String writerId;

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
            String centerId,
            String content,
            List<PostContents> contentList,
            String writerId
    ) {
        this.centerId = centerId;
        this.content = content;
        this.writerId = writerId;
        this.contentList = contentList;
        this.isDeleted = false;
    }

    private Post(
            String centerId,
            String content,
            String writerId,
            List<PostContents> contentList,
            List<ClimbingHistory> climbingHistoryList
    ) {
        this.centerId = centerId;
        this.content = content;
        this.isDeleted = false;
        this.writerId = writerId;
        this.contentList = contentList;
        this.climbingHistoryList = climbingHistoryList;
    }

    public static Post of(
            String centerId,
            String content,
            List<PostContents> contentList,
            String writerId
    ) {
        return new Post(
                centerId,
                content,
                contentList,
                writerId
        );
    }

    public static Post of(
            String centerId,
            String content,
            String writerId,
            List<PostContents> contentsList,
            List<ClimbingHistory> climbingHistoryList
    ) {
        return new Post(
                centerId,
                content,
                writerId,
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

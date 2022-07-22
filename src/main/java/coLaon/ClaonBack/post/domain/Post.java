package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Table(name = "tb_post")
@NoArgsConstructor
public class Post extends BaseEntity {
    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;
    @Column(name = "content", length = 500)
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PostContents> contentsSet;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ClimbingHistory> climbingHistorySet;

    private Post(
            Center center,
            String content,
            User writer
    ) {
        this.center = center;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentsSet = Set.of();
        this.climbingHistorySet = Set.of();
    }

    private Post(
            String id,
            Center center,
            String content,
            User writer,
            Set<PostContents> contentsSet,
            Set<ClimbingHistory> climbingHistorySet
    ) {
        super(id);
        this.center = center;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentsSet = contentsSet;
        this.climbingHistorySet = climbingHistorySet;
    }

    private Post(
            String id,
            Center center,
            String content,
            User writer,
            Set<PostContents> contentsSet,
            Set<ClimbingHistory> climbingHistorySet,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.center = center;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentsSet = contentsSet;
        this.climbingHistorySet = climbingHistorySet;
    }

    public static Post of(
            Center center,
            String content,
            User writer
    ) {
        return new Post(
                center,
                content,
                writer
        );
    }

    public static Post of(
            String id,
            Center center,
            String content,
            User writer,
            Set<PostContents> contentsSet,
            Set<ClimbingHistory> climbingHistorySet
    ) {
        return new Post(
                id,
                center,
                content,
                writer,
                contentsSet,
                climbingHistorySet
        );
    }

    public static Post of(
            String id,
            Center center,
            String content,
            User writer,
            Set<PostContents> contentsSet,
            Set<ClimbingHistory> climbingHistorySet,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Post(
                id,
                center,
                content,
                writer,
                contentsSet,
                climbingHistorySet,
                createdAt,
                updatedAt
        );
    }

    public void delete() {
        this.isDeleted = true;
    }
}

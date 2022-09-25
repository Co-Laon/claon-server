package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.post.domain.converter.PostContentsConverter;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
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

    @Convert(converter = PostContentsConverter.class)
    @Column(name = "content_list", length = 2000)
    private List<PostContents> contentList;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ClimbingHistory> climbingHistorySet;

    public String getThumbnailUrl() {
        if (this.getContentList().size() == 0) {
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
            Set<ClimbingHistory> climbingHistorySet
    ) {
        this.center = center;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentList = contentList;
        this.climbingHistorySet = climbingHistorySet;
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
            List<PostContents> contentsSet,
            Set<ClimbingHistory> climbingHistorySet
    ) {
        return new Post(
                center,
                content,
                writer,
                contentsSet,
                climbingHistorySet
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

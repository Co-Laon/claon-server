package coLaon.ClaonBack.post.domain;

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
import javax.persistence.Table;
import java.util.Set;

@Entity
@Getter
@Table(name = "tb_post")
@NoArgsConstructor
public class Post extends BaseEntity {
    @Column(name = "center_name", nullable = false)
    private String centerName;
    @Column(name = "hold_info")
    private String holdInfo;
    @Column(name = "content", length = 500)
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PostContents> contentsSet;

    private Post(
            String centerName,
            String holdInfo,
            String content,
            User writer
    ) {
        this.centerName = centerName;
        this.holdInfo = holdInfo;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentsSet = Set.of();
    }

    private Post(
            String id,
            String centerName,
            String holdInfo,
            String content,
            User writer,
            Set<PostContents> contentsSet
    ) {
        super(id);
        this.centerName = centerName;
        this.holdInfo = holdInfo;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.contentsSet = contentsSet;
    }

    public static Post of(
            String centerName,
            String holdInfo,
            String content,
            User writer
    ) {
        return new Post(
                centerName,
                holdInfo,
                content,
                writer
        );
    }

    public static Post of(
            String id,
            String centerName,
            String holdInfo,
            String content,
            User writer,
            Set<PostContents> contentsSet
    ) {
        return new Post(
                id,
                centerName,
                holdInfo,
                content,
                writer,
                contentsSet
        );
    }
}

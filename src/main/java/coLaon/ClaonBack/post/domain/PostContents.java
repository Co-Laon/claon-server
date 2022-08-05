package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_post_contents")
@NoArgsConstructor
public class PostContents extends BaseEntity {
    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @Column(name = "url", nullable = false, length = 500)
    private String url;

    private PostContents(
            Post post,
            String url
    ) {
        this.post = post;
        this.url = url;
    }

    public static PostContents of(
            Post post,
            String url
    ) {
        return new PostContents(
                post,
                url
        );
    }
}

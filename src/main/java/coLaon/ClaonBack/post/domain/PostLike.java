package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_post_like")
@NoArgsConstructor
public class PostLike extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User liker;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private PostLike(
            User liker,
            Post post
    ) {
        this.liker = liker;
        this.post = post;
    }

    private PostLike(
            String id,
            User liker,
            Post post
    ) {
        super(id);
        this.liker = liker;
        this.post = post;
    }

    public static PostLike of(
            User liker,
            Post post
    ) {
        return new PostLike(
                liker,
                post
        );
    }

    public static PostLike of(
            String id,
            User liker,
            Post post
    ) {
        return new PostLike(
                id,
                liker,
                post
        );
    }
}
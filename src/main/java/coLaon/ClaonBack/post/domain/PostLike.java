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
@Table(name = "tb_laon_like")
@NoArgsConstructor
public class LaonLike extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User liker;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "laon_id", nullable = false)
    private Post post;

    private LaonLike(
            User liker,
            Post post
    ) {
        this.liker = liker;
        this.post = post;
    }

    private LaonLike(
            String id,
            User liker,
            Post post
    ) {
        super(id);
        this.liker = liker;
        this.post = post;
    }

    public static LaonLike of(
            User liker,
            Post post
    ) {
        return new LaonLike(
                liker,
                post
        );
    }

    public static LaonLike of(
            String id,
            User liker,
            Post post
    ) {
        return new LaonLike(
                id,
                liker,
                post
        );
    }
}

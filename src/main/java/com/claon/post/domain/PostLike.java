package com.claon.post.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "tb_post_like")
@NoArgsConstructor
public class PostLike extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User liker;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private PostLike(
            User liker,
            Post post
    ) {
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
}
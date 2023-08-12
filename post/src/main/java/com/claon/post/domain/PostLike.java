package com.claon.post.domain;

import com.claon.post.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "tb_post_like")
@NoArgsConstructor
public class PostLike extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private String likerId;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private PostLike(
            String likerId,
            Post post
    ) {
        this.likerId = likerId;
        this.post = post;
    }

    public static PostLike of(
            String likerId,
            Post post
    ) {
        return new PostLike(
                likerId,
                post
        );
    }
}
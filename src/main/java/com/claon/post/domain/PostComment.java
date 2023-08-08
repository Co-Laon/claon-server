package com.claon.post.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Table(name = "tb_post_comment")
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @Column(name = "content")
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @ManyToOne(targetEntity = PostComment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PostComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<PostComment> childComments;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private PostComment(
            String content,
            User writer,
            Post post,
            PostComment parentComment
    ) {
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.post = post;
        this.parentComment = parentComment;
    }

    public static PostComment of(
            String content,
            User writer,
            Post post,
            PostComment parentComment
    ) {
        return new PostComment(
                content,
                writer,
                post,
                parentComment
        );
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }
}

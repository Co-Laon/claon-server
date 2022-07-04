package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_post_comment")
@Where(clause = "is_deleted = false")
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @Column(name = "content")
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    @ManyToOne(targetEntity = PostComment.class)
    @JoinColumn(name = "parent_comment_id")
    private PostComment parentComment;
    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", nullable = false)
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

    private PostComment(
            String id,
            String content,
            User writer,
            Post post,
            PostComment parentComment

    ) {
        super(id);
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

    public static PostComment of(
            String id,
            String content,
            User writer,
            Post post,
            PostComment parentComment
    ) {
        return new PostComment(
                id,
                content,
                writer,
                post,
                parentComment
        );
    }

    public void updateContent(String content){
        this.content = content;
    }
    public void deleteContent(){
        this.isDeleted = true;
    }
}

package coLaon.ClaonBack.laon.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_laon_comment")
@NoArgsConstructor
public class LaonComment extends BaseEntity {
    @Column(name = "content")
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    @ManyToOne(targetEntity = LaonComment.class)
    @JoinColumn(name = "parent_comment_id")
    private LaonComment parentComment;

    private LaonComment(
            String content,
            User writer,
            LaonComment parentComment
    ) {
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
        this.parentComment = parentComment;
    }

    public static LaonComment of(
            String content,
            User writer,
            LaonComment parentComment
    ) {
        return new LaonComment(
                content,
                writer,
                parentComment
        );
    }
}

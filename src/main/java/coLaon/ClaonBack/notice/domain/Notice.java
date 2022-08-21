package coLaon.ClaonBack.notice.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@RequiredArgsConstructor
public class Notice extends BaseEntity {

    private String title;
    private String content;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="writer_id")
    private User writer;

    private Notice(String title, String content, User writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Notice of(
            String title,
            String content,
            User writer
    ) {
        return new Notice(title, content, writer);
    }
}

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
@Table(name = "tb_laon")
@NoArgsConstructor
public class Laon extends BaseEntity {
    @Column(name = "center_name", nullable = false)
    private String centerName;
    @Column(name = "wall_name")
    private String wallName;
    @Column(name = "hold_info")
    private String holdInfo;
    @Column(name = "video_url", nullable = false)
    private String videoUrl;
    @Column(name = "video_thumbnail_url")
    private String videoThumbnailUrl;
    @Column(name = "content")
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    private Laon(
            String centerName,
            String wallName,
            String holdInfo,
            String videoUrl,
            String videoThumbnailUrl,
            String content,
            User writer
    ) {
        this.centerName = centerName;
        this.wallName = wallName;
        this.holdInfo = holdInfo;
        this.videoUrl = videoUrl;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.content = content;
        this.isDeleted = false;
        this.writer = writer;
    }

    public static Laon of(
            String centerName,
            String wallName,
            String holdInfo,
            String videoUrl,
            String videoThumbnailUrl,
            String content,
            User writer
    ) {
        return new Laon(
                centerName,
                wallName,
                holdInfo,
                videoUrl,
                videoThumbnailUrl,
                content,
                writer
        );
    }
}

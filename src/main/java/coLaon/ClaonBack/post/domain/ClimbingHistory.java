package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "tb_climbing_history")
@NoArgsConstructor
public class ClimbingHistory extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "hold_info_id", nullable = false)
    private HoldInfo holdInfo;

    private ClimbingHistory(Post post, HoldInfo holdInfo) {
        this.post = post;
        this.holdInfo = holdInfo;
    }

    private ClimbingHistory(String id, Post post, HoldInfo holdInfo) {
        super(id);
        this.post = post;
        this.holdInfo = holdInfo;
    }

    public static ClimbingHistory of(Post post, HoldInfo holdInfo) {
        return new ClimbingHistory(
                post,
                holdInfo
        );
    }

    public static ClimbingHistory of(String id, Post post, HoldInfo holdInfo) {
        return new ClimbingHistory(
                id,
                post,
                holdInfo
        );
    }
}

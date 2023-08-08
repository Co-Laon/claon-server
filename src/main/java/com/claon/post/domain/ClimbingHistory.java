package com.claon.post.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.center.domain.HoldInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "tb_climbing_history")
@NoArgsConstructor
public class ClimbingHistory extends BaseEntity {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "hold_info_id", nullable = false)
    private HoldInfo holdInfo;

    @Column(name = "climbing_count", nullable = false)
    private Integer climbingCount;

    private ClimbingHistory(
            Post post,
            HoldInfo holdInfo,
            Integer climbingCount
    ) {
        this.post = post;
        this.holdInfo = holdInfo;
        this.climbingCount = climbingCount;
    }

    public static ClimbingHistory of(
            Post post,
            HoldInfo holdInfo,
            Integer climbingCount
    ) {
        return new ClimbingHistory(
                post,
                holdInfo,
                climbingCount
        );
    }
}

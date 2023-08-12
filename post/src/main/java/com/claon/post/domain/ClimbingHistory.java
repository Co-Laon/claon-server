package com.claon.post.domain;

import com.claon.post.common.domain.BaseEntity;
import jakarta.persistence.*;
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

    @Column(name = "hold_info_id", nullable = false)
    private String holdInfoId;

    @Column(name = "climbing_count", nullable = false)
    private Integer climbingCount;

    private ClimbingHistory(
            Post post,
            String holdInfoId,
            Integer climbingCount
    ) {
        this.post = post;
        this.holdInfoId = holdInfoId;
        this.climbingCount = climbingCount;
    }

    public static ClimbingHistory of(
            Post post,
            String holdInfoId,
            Integer climbingCount
    ) {
        return new ClimbingHistory(
                post,
                holdInfoId,
                climbingCount
        );
    }
}

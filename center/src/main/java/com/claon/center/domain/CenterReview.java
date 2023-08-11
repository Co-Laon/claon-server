package com.claon.center.domain;

import com.claon.center.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "tb_center_review")
@NoArgsConstructor
public class CenterReview extends BaseEntity {
    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "user_id", nullable = false)
    private String writerId;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Center center;

    private CenterReview(
            Integer rank,
            String content,
            String writerId,
            Center center
    ) {
        this.rank = rank;
        this.content = content;
        this.writerId = writerId;
        this.center = center;
    }

    public static CenterReview of(
            Integer rank,
            String content,
            String writerId,
            Center center
    ) {
        return new CenterReview(
                rank,
                content,
                writerId,
                center
        );
    }

    public void update(Integer rank, String content) {
        this.rank = rank;
        this.content = content;
    }
}

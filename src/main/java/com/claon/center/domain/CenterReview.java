package com.claon.center.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_center_review")
@NoArgsConstructor
public class CenterReview extends BaseEntity {
    @Column(name = "rank", nullable = false)
    private Integer rank;
    @Column(name = "content", length = 500)
    private String content;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;
    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Center center;

    private CenterReview(
            Integer rank,
            String content,
            User writer,
            Center center
    ) {
        this.rank = rank;
        this.content = content;
        this.writer = writer;
        this.center = center;
    }

    public static CenterReview of(
            Integer rank,
            String content,
            User writer,
            Center center
    ) {
        return new CenterReview(
                rank,
                content,
                writer,
                center
        );
    }

    public void update(Integer rank, String content) {
        this.rank = rank;
        this.content = content;
    }
}

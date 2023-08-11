package com.claon.center.domain;

import com.claon.center.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "tb_center_bookmark")
@NoArgsConstructor
public class CenterBookmark extends BaseEntity {
    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Center center;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private CenterBookmark(
            Center center,
            String userId
    ) {
        this.center = center;
        this.userId = userId;
    }

    public static CenterBookmark of(
            Center center,
            String userId
    ) {
        return new CenterBookmark(center, userId);
    }
}

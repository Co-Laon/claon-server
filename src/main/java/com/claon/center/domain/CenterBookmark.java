package com.claon.center.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private CenterBookmark(
            Center center,
            User user
    ) {
        this.center = center;
        this.user = user;
    }

    public static CenterBookmark of(
            Center center,
            User user
    ) {
        return new CenterBookmark(center, user);
    }
}

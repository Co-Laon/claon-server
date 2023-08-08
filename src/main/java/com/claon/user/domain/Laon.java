package com.claon.user.domain;

import com.claon.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "tb_laon")
@NoArgsConstructor
public class Laon extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "laon_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User laon;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public static final String domain = "라온";

    private Laon(
            User user,
            User laon
    ) {
        this.laon = laon;
        this.user = user;
    }

    public static Laon of(
            User user,
            User laon
    ) {
        return new Laon(
                user,
                laon
        );
    }
}

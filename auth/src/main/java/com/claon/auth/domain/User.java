package com.claon.auth.domain;

import com.claon.auth.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tb_user")
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "height")
    private Float height;

    @Column(name = "arm_reach")
    private Float armReach;

    @Column(name = "is_private")
    private Boolean isPrivate;

    private User(
            String email,
            String nickname,
            Float height,
            Float armReach
    ) {
        this.email = email;
        this.nickname = nickname;
        this.height = height;
        this.armReach = armReach;
        this.isPrivate = false;
    }

    public static User of(
            String email,
            String nickname,
            Float height,
            Float armReach
    ) {
        return new User(
                email,
                nickname,
                height,
                armReach
        );
    }

    public static User signUp(
            String email,
            String nickname,
            Float height,
            Float armReach
    ) {
        return new User(
                email,
                nickname,
                height,
                armReach
        );
    }
}

package com.claon.gateway.domain;

import com.claon.gateway.common.domain.BaseEntity;
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

    @Column(name = "oauth_id", nullable = false)
    private String oAuthId;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "height")
    private Float height;

    @Column(name = "arm_reach")
    private Float armReach;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "instagram_oauth_id")
    private String instagramOAuthId;

    @Column(name = "instagram_user_name")
    private String instagramUserName;

    @Column(name = "is_private")
    private Boolean isPrivate;

    private User(
            String email,
            String oAuthId,
            String nickname,
            Float height,
            Float armReach,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        this.email = email;
        this.oAuthId = oAuthId;
        this.nickname = nickname;
        this.height = height;
        this.armReach = armReach;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isPrivate = false;
    }

    public static User of(
            String email,
            String oAuthId,
            String nickname,
            Float height,
            Float armReach,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        return new User(
                email,
                oAuthId,
                nickname,
                height,
                armReach,
                imagePath,
                instagramOAuthId,
                instagramUserName
        );
    }

    public static User signUp(
            String email,
            String oAuthId,
            String nickname,
            Float height,
            Float armReach,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        return new User(
                email,
                oAuthId,
                nickname,
                height,
                armReach,
                imagePath,
                instagramOAuthId,
                instagramUserName
        );
    }
}

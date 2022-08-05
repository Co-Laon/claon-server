package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.UUID;

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
    @Column(name = "metropolitan_active_area")
    private String metropolitanActiveArea;
    @Column(name = "basic_local_active_area")
    private String basicLocalActiveArea;
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
            String nickname,
            String oAuthId
    ) {
        this.email = email;
        this.nickname = nickname;
        this.oAuthId = oAuthId;
        this.isPrivate = false;
    }

    private User(
            String email,
            String oAuthId,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        this.email = email;
        this.oAuthId = oAuthId;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isPrivate = false;
    }

    public static User createNewUser(
            String email,
            String oAuthId
    ) {
        return new User(
                email,
                UUID.randomUUID().toString(),
                oAuthId
        );
    }

    public static User of(
            String email,
            String oAuthId,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        return new User(
                email,
                oAuthId,
                nickname,
                metropolitanActiveArea,
                basicLocalActiveArea,
                imagePath,
                instagramOAuthId,
                instagramUserName
        );
    }

    public void signUp(
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
    }

    public Boolean isSignupCompleted() {
        try {
            // when nickname is type of UUID,
            // user is not yet complete Signup
            UUID.fromString(this.getNickname());
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public void changePublicScope() {
        this.isPrivate = !this.isPrivate;
    }

    public void modifyUser(
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramUserName,
            String instagramOAuthId
    ) {
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramUserName = instagramUserName;
        this.instagramOAuthId = instagramOAuthId;
    }
}

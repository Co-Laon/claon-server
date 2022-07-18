package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.UUID;

@Entity
@Getter
@Setter
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
    @Column(name = "is_deleted")
    private Boolean isDeleted;
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
    }

    private User(
            String id,
            String email,
            String oAuthId,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramOAuthId,
            String instagramUserName
    ) {
        super(id);
        this.email = email;
        this.oAuthId = oAuthId;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramOAuthId = instagramOAuthId;
        this.instagramUserName = instagramUserName;
        this.isDeleted = false;
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
        this.isDeleted = false;
        this.isPrivate = false;
    }

    public static User createNewUser(
            String email,
            String oAuthId
    ) {
        String autoGeneratedNickname = UUID.randomUUID().toString();
        return new User(email, autoGeneratedNickname, oAuthId);
    }

    public static User createNewUser(
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

    public static User createNewUser(
            String id,
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
                id,
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

    public Boolean isSignupCompleted(){
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

    public void modifyUser(UserModifyRequestDto dto) {
        this.imagePath = dto.getImagePath();
        this.nickname = dto.getNickname();
        this.basicLocalActiveArea = dto.getBasicLocalActiveArea();
        this.metropolitanActiveArea = dto.getMetropolitanActiveArea();
        this.instagramUserName = dto.getInstagramUserName();
        this.instagramOAuthId = dto.getInstagramOAuthId();
        this.isPrivate = dto.getIsPrivate();
    }
}

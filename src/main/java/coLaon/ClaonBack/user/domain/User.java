package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Getter
@Table(name = "tb_user")
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "phoneNumber", nullable = false, unique = true)
    private String phoneNumber;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;
    @Column(name = "wideActiveArea")
    private String wideActiveArea;
    @Column(name = "narrowActiveArea")
    private String narrowActiveArea;
    @Column(name = "image")
    private String imagePath;
    @Column(name = "instagramId")
    private String instagramId;
    @Column(name = "isDeleted")
    private Boolean isDeleted;

    public User(
            String phoneNumber,
            String email,
            String password,
            String nickname,
            String wideActiveArea,
            String narrowActiveArea,
            String imagePath,
            String instagramId
    ) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.wideActiveArea = wideActiveArea;
        this.narrowActiveArea = narrowActiveArea;
        this.imagePath = imagePath;
        this.instagramId = instagramId;
        this.isDeleted = false;
    }

    public static User of(
            String phoneNumber,
            String email,
            String password,
            String nickname,
            String wideActiveArea,
            String narrowActiveArea,
            String imagePath,
            String instagramId
    )
    {
        return new User(phoneNumber, email, password, nickname, wideActiveArea, narrowActiveArea, imagePath, instagramId);
    }
}

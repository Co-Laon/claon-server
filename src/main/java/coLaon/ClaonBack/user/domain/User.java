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
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;
    @Column(name = "metropolitan_active_area")
    private String metropolitanActiveArea;
    @Column(name = "basic_local_active_area")
    private String basicLocalActiveArea;
    @Column(name = "image")
    private String imagePath;
    @Column(name = "instagramId")
    private String instagramId;
    @Column(name = "isDeleted")
    private Boolean isDeleted;

    private User(
            String phoneNumber,
            String email,
            String password,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramId
    ) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.metropolitanActiveArea = metropolitanActiveArea;
        this.basicLocalActiveArea = basicLocalActiveArea;
        this.imagePath = imagePath;
        this.instagramId = instagramId;
        this.isDeleted = false;
    }

    public static User of(
            String phoneNumber,
            String email,
            String password,
            String nickname,
            String metropolitanActiveArea,
            String basicLocalActiveArea,
            String imagePath,
            String instagramId
    )
    {
        return new User(
                phoneNumber,
                email,
                password,
                nickname,
                metropolitanActiveArea,
                basicLocalActiveArea,
                imagePath,
                instagramId
        );
    }
}

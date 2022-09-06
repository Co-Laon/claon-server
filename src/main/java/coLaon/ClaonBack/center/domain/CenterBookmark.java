package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_center_bookmark")
@NoArgsConstructor
public class CenterBookmark extends BaseEntity {
    @ManyToOne(targetEntity = Center.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;
    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
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

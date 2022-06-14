package coLaon.ClaonBack.laon.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_laon_like")
@NoArgsConstructor
public class LaonLike extends BaseEntity {
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User liker;

    @ManyToOne(targetEntity = Laon.class)
    @JoinColumn(name = "laon_id", nullable = false)
    private Laon laon;

    private LaonLike(
            User liker,
            Laon laon
    ) {
        this.liker = liker;
        this.laon = laon;
    }

    public static LaonLike of(
            User liker,
            Laon laon
    ) {
        return new LaonLike(
                liker,
                laon
        );
    }
}

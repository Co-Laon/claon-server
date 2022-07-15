package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "tb_laon")
@NoArgsConstructor
public class Laon extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "laon_id", nullable = false)
    private User laon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Laon(User laon, User user) {
        this.laon = laon;
        this.user = user;
    }

    public static Laon of(User laon, User user) {
        return new Laon(
                laon,
                user
        );
    }
}

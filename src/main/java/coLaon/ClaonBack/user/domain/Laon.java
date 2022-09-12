package coLaon.ClaonBack.user.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
    private User laon;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
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

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
@Table(name = "tb_block_user")
@NoArgsConstructor
public class BlockUser extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "block_user_id", nullable = false)
    private User blockedUser;

    private BlockUser(User user, User blockedUser) {
        this.user = user;
        this.blockedUser = blockedUser;
    }

    public static BlockUser of(User user, User blockedUser) {
        return new BlockUser(user, blockedUser);
    }
}

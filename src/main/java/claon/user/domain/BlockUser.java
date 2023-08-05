package claon.user.domain;

import claon.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "tb_block_user")
@NoArgsConstructor
public class BlockUser extends BaseEntity {
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "block_user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User blockedUser;

    public static final String domain = "차단";

    private BlockUser(
            User user,
            User blockedUser
    ) {
        this.user = user;
        this.blockedUser = blockedUser;
    }

    public static BlockUser of(
            User user,
            User blockedUser
    ) {
        return new BlockUser(user, blockedUser);
    }
}

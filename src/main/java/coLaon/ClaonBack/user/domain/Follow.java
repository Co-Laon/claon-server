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
@Table(name = "tb_follow")
@NoArgsConstructor
public class Follow extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "follower")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following")
    private User following;

    private Follow(User follower, User following){
        this.follower = follower;
        this.following = following;
    }

    public static Follow of(User follower, User following) {
        return new Follow(
                follower,
                following
        );
    }
}

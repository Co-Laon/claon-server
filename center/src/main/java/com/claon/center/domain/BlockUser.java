package com.claon.center.domain;

import com.claon.center.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_block_user")
@NoArgsConstructor
public class BlockUser extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "block_user_id", nullable = false)
    private String blockedUserId;

    private BlockUser(String userId, String blockedUserId) {
        this.userId = userId;
        this.blockedUserId = blockedUserId;
    }

    public static BlockUser of(String userId, String blockedUserId) {
        return new BlockUser(userId, blockedUserId);
    }
}

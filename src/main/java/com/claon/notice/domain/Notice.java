package com.claon.notice.domain;

import com.claon.common.domain.BaseEntity;
import com.claon.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "tb_notice")
@RequiredArgsConstructor
public class Notice extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "content", length = 1000)
    private String content;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "writer_id")
    private User writer;

    private Notice(String title, String content, User writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Notice of(
            String title,
            String content,
            User writer
    ) {
        return new Notice(title, content, writer);
    }
}
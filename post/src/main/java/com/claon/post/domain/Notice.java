package com.claon.post.domain;

import com.claon.post.common.domain.BaseEntity;
import jakarta.persistence.*;
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

    @Column(name = "writer_id")
    private String writerId;

    private Notice(String title, String content, String writerId) {
        this.title = title;
        this.content = content;
        this.writerId = writerId;
    }

    public static Notice of(
            String title,
            String content,
            String writerId
    ) {
        return new Notice(title, content, writerId);
    }
}

package com.claon.center.domain;

import com.claon.center.domain.enums.CenterReportType;
import com.claon.center.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "tb_center_report")
@NoArgsConstructor
public class CenterReport extends BaseEntity {
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CenterReportType reportType;

    @Column(name = "user_id")
    private String reporterId;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Center center;

    private CenterReport(
            String content,
            CenterReportType reportType,
            String reporterId,
            Center center
    ) {
        this.content = content;
        this.reportType = reportType;
        this.reporterId = reporterId;
        this.center = center;
    }

    public static CenterReport of(
            String content,
            CenterReportType reportType,
            String reporterId,
            Center center
    ) {
        return new CenterReport(
                content,
                reportType,
                reporterId,
                center
        );
    }
}

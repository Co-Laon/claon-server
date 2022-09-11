package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.center.domain.enums.CenterReportType;
import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User reporter;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    private CenterReport(
            String content,
            CenterReportType reportType,
            User reporter,
            Center center
    ) {
        this.content = content;
        this.reportType = reportType;
        this.reporter = reporter;
        this.center = center;
    }

    public static CenterReport of(
            String content,
            CenterReportType reportType,
            User reporter,
            Center center
    ) {
        return new CenterReport(
                content,
                reportType,
                reporter,
                center
        );
    }
}

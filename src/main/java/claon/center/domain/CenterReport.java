package claon.center.domain;

import claon.center.domain.enums.CenterReportType;
import claon.common.domain.BaseEntity;
import claon.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumn(name = "user_id")
    private User reporter;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

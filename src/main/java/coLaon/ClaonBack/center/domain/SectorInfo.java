package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Table(name = "tb_sector_info")
@NoArgsConstructor
public class SectorInfo extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "start")
    private LocalDate start;
    @Column(name = "end")
    private LocalDate end;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    private SectorInfo(
            String name,
            LocalDate start,
            LocalDate end,
            Center center
    ) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.center = center;
    }

    public static SectorInfo of(
            String name,
            LocalDate start,
            LocalDate end,
            Center center
    ) {
        return new SectorInfo(
                name,
                start,
                end,
                center
        );
    }

    public static SectorInfo of(
            String name,
            String start,
            String end,
            Center center
    ) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (!start.equals("")) {
            startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy/M/d"));
        }

        if (!end.equals("")) {
            endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy/M/d"));
        }

        return new SectorInfo(
                name,
                startDate,
                endDate,
                center
        );
    }
}

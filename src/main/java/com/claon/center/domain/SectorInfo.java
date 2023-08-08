package com.claon.center.domain;

import com.claon.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Table(name = "tb_sector_info")
@NoArgsConstructor
public class SectorInfo extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_time")
    private LocalDate start;

    @Column(name = "end_time")
    private LocalDate end;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
        if (!start.isEmpty()) {
            startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy/M/d"));
        }

        if (!end.isEmpty()) {
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

package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "tb_hold_info")
@NoArgsConstructor
public class HoldInfo extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "img_url", nullable = false, length = 500)
    private String img;

    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Center center;

    private HoldInfo(
            String name,
            String img,
            Center center
    ) {
        this.name = name;
        this.img = img;
        this.center = center;
    }

    public static HoldInfo of(
            String name,
            String img,
            Center center
    ) {
        return new HoldInfo(name, img, center);
    }
}

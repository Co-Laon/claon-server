package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tb_center")
@NoArgsConstructor
public class Center extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "tel")
    private String tel;
    @Column(name = "web")
    private String web;
    @Column(name = "instagram")
    private String instagram;
    @Column(name = "youtube")
    private String youtube;
    @Convert(converter = CenterImgListConverter.class)
    @Column(name = "img_list")
    private List<CenterImg> imgList;
    @Convert(converter = OperatingTimeListConverter.class)
    @Column(name = "operating_time")
    private List<OperatingTime> operatingTime;
    @Column(name = "facilities")
    private String facilities;
    @Convert(converter = ChargeListConverter.class)
    @Column(name = "charge")
    private List<Charge> charge;
    @Column(name = "charge_img")
    private String chargeImg;
    @Column(name = "hold_info_img")
    private String holdInfoImg;
    @Convert(converter = SectorInfoListConverter.class)
    @Column(name = "sector_info")
    private List<SectorInfo> sectorInfo;

    private Center(
            String name,
            String address,
            String tel,
            String web,
            String instagram,
            String youtube,
            List<CenterImg> imgList,
            List<OperatingTime> operatingTime,
            String facilities,
            List<Charge> charge,
            String chargeImg,
            String holdInfoImg,
            List<SectorInfo> sectorInfo
    ) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.web = web;
        this.instagram = instagram;
        this.youtube = youtube;
        this.imgList = imgList;
        this.operatingTime = operatingTime;
        this.facilities = facilities;
        this.charge = charge;
        this.chargeImg = chargeImg;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfo = sectorInfo;
    }

    public static Center of(
            String name,
            String address,
            String tel,
            String web,
            String instagram,
            String youtube,
            List<CenterImg> imgList,
            List<OperatingTime> operatingTimeList,
            String facilities,
            List<Charge> chargeList,
            String chargeImg,
            String holdInfoImg,
            List<SectorInfo> sectorInfoList
    ) {
        return new Center(
                name,
                address,
                tel,
                web,
                instagram,
                youtube,
                imgList,
                operatingTimeList,
                facilities,
                chargeList,
                chargeImg,
                holdInfoImg,
                sectorInfoList
        );
    }
}

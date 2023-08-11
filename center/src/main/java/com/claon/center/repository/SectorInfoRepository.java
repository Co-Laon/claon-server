package com.claon.center.repository;

import com.claon.center.domain.Center;
import com.claon.center.domain.SectorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorInfoRepository extends JpaRepository<SectorInfo, String> {
    List<SectorInfo> findAllByCenter(Center center);
}

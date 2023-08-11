package com.claon.center.repository;

import com.claon.center.domain.Center;
import com.claon.center.domain.HoldInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldInfoRepository extends JpaRepository<HoldInfo, String> {
    List<HoldInfo> findAllByCenter(Center center);
    Optional<HoldInfo> findByIdAndCenter(String id, Center center);
}

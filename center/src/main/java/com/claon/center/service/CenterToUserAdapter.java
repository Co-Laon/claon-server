package com.claon.center.service;

import com.claon.center.repository.CenterRepository;
//import com.claon.user.service.CenterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CenterToUserAdapter {

    private final CenterRepository centerRepository;

    public Boolean existsByCenterId(String centerId) {
        return this.centerRepository.existsById(centerId);
    }
}

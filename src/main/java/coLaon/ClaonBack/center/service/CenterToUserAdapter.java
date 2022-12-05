package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.user.service.CenterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CenterToUserAdapter implements CenterPort {

    private final CenterRepository centerRepository;

    @Override
    public Boolean existsByCenterId(String centerId) {
        return this.centerRepository.existsById(centerId);
    }
}

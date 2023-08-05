package claon.center.service;

import claon.center.repository.CenterRepository;
import claon.user.service.CenterPort;
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

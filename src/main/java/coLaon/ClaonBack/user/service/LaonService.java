package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.LaonResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaonService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;

    @Transactional
    public LaonResponseDto laon(String laonId, String userId) {
        User laon = userRepository.findById(laonId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        laonRepository.findByLaonAndUser(laon, user).ifPresent(
                follow -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 팔로우한 관계입니다."
                    );
                }
        );

        return LaonResponseDto.from(laonRepository.save(Laon.of(laon,user)));
    }

    @Transactional
    public LaonResponseDto unlaon(String laonId, String userId) {
        User laon = userRepository.findById(laonId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Laon laonRelation = laonRepository.findByLaonAndUser(laon, user).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "팔로우 관계가 아닙니다."
                )
        );
        laonRepository.delete(laonRelation);
        return LaonResponseDto.from(laonRelation);
    }
}

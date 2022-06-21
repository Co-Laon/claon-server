package coLaon.ClaonBack.laon.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.laon.domain.LaonLike;
import coLaon.ClaonBack.laon.dto.LaonCreateRequestDto;
import coLaon.ClaonBack.laon.dto.LaonResponseDto;
import coLaon.ClaonBack.laon.dto.LikeRequestDto;
import coLaon.ClaonBack.laon.dto.LikeResponseDto;
import coLaon.ClaonBack.laon.repository.LaonLikeRepository;
import coLaon.ClaonBack.laon.repository.LaonRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LaonService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;
    private final LaonLikeRepository laonLikeRepository;

    @Transactional
    public LikeResponseDto createLike(String userId, LikeRequestDto likeRequestDto) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Laon laon = laonRepository.findById(likeRequestDto.getLaonId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return LikeResponseDto.from(laonLikeRepository.save(
                LaonLike.of(liker, laon))
        );
    }

    @Transactional
    public LaonResponseDto createLaon(String userId, LaonCreateRequestDto laonCreateRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        return LaonResponseDto.from(laonRepository.save(
                Laon.of(
                        laonCreateRequestDto.getCenterName(),
                        laonCreateRequestDto.getWallName(),
                        laonCreateRequestDto.getHoldInfo(),
                        laonCreateRequestDto.getVideoUrl(),
                        laonCreateRequestDto.getVideoThumbnailUrl(),
                        laonCreateRequestDto.getContent(),
                        writer
                ))
        );
    }

}

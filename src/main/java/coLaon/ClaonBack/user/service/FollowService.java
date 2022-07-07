package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.Follow;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.FollowResponseDto;
import coLaon.ClaonBack.user.repository.FollowRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public FollowResponseDto follow(String followerId, String followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        User following = userRepository.findById(followingId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        followRepository.findByFollowerAndFollowing(follower, following).ifPresent(
                follow -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 팔로우한 관계입니다."
                    );
                }
        );

        return FollowResponseDto.from(followRepository.save(Follow.of(follower,following)));
    }

    @Transactional
    public FollowResponseDto unfollow(String followerId, String followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        User following = userRepository.findById(followingId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "팔로우 관계가 아닙니다."
                )
        );
        followRepository.delete(follow);
        return FollowResponseDto.from(follow);
    }
}

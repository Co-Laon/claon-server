package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.enums.BasicLocalArea;
import coLaon.ClaonBack.common.domain.enums.MetropolitanArea;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(String nickname) {
        return DuplicatedCheckResponseDto.of(this.userRepository.findByNickname(nickname).isPresent());
    }

    @Transactional
    public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        this.userRepository.findByEmail(signUpRequestDto.getEmail()).ifPresent(
                email -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 존재하는 이메일입니다."
                    );
                }
        );

        this.userRepository.findByNickname(signUpRequestDto.getNickname()).ifPresent(
                nickname -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 존재하는 닉네임입니다."
                    );
                }
        );

        return UserResponseDto.from(userRepository.save(
                User.of(
                        signUpRequestDto.getEmail(),
                        signUpRequestDto.getOAuthId(),
                        signUpRequestDto.getNickname(),
                        MetropolitanArea.of(signUpRequestDto.getMetropolitanActiveArea()),
                        BasicLocalArea.of(
                                signUpRequestDto.getMetropolitanActiveArea(),
                                signUpRequestDto.getBasicLocalActiveArea()
                        ),
                        signUpRequestDto.getImagePath(),
                        signUpRequestDto.getInstagramOAuthId(),
                        signUpRequestDto.getInstagramUserName()
                ))
        );
    }
}



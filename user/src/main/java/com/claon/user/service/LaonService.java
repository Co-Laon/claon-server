package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.BadRequestException;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.NotFoundException;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.common.validator.NotIdEqualValidator;
import com.claon.user.domain.Laon;
import com.claon.user.domain.User;
import com.claon.user.dto.LaonResponseDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.LaonRepositorySupport;
import com.claon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaonService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;
    private final BlockUserRepository blockUserRepository;
    private final LaonRepositorySupport laonRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Transactional
    public void createLaon(RequestUserInfo userInfo, String laonId) {
        User user = findUserById(userInfo.id());

        User laon = findUserById(laonId);

        NotIdEqualValidator.of(user.getId(), laon.getId(), Laon.domain).validate();

        validateBlockUser(userInfo, laonId);

        laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).ifPresent(
                l -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            String.format("%s을 이미 라온했습니다.", l.getId())
                    );
                }
        );

        laonRepository.save(Laon.of(user, laon));
    }

    @Transactional
    public void deleteLaon(RequestUserInfo userInfo, String laonId) {
        User user = findUserById(userInfo.id());

        User laon = findUserById(laonId);

        Laon laonRelation = laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 아직 라온하지 않았습니다.", laonId)
                )
        );

        laonRepository.delete(laonRelation);
    }

    @Transactional(readOnly = true)
    public Pagination<LaonResponseDto> findAllLaon(RequestUserInfo userInfo, Pageable pageable) {
        return this.paginationFactory.create(
                laonRepositorySupport.findAllByUserId(userInfo.id(), pageable)
        );
    }

    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", id)
                )
        );
    }

    private void validateBlockUser(RequestUserInfo userInfo, String laonId) {
        if (!blockUserRepository.findBlock(userInfo.id(), laonId).isEmpty()) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    String.format("%s을 찾을 수 없습니다.", laonId)
            );
        }
    }
}

package com.claon.user.web;

import com.claon.user.common.domain.Pagination;
import com.claon.user.dto.*;
import com.claon.user.service.BlockUserService;
import com.claon.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final BlockUserService blockUserService;

    @PutMapping("/me/scope")
    @ResponseStatus(value = HttpStatus.OK)
    public PublicScopeResponseDto changePublicScope(
            @RequestHeader(value = "X-USER-ID") String userId
    ) {
        return this.userService.changePublicScope(userId);
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailResponseDto retrieveMe(@RequestHeader(value = "X-USER-ID") String userId) {
        return userService.retrieveMe(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPreviewResponseDto> searchUser(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestParam String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.searchUser(userId, nickname, pageable);
    }

    @PutMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto modifyUser(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestBody @Valid UserModifyRequestDto dto
    ) {
        return userService.modifyUser(userId, dto);
    }

    @GetMapping("/name/{userNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public IndividualUserResponseDto getPublicUser(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String userNickname
    ) {
        return userService.getOtherUserInformation(userId, userNickname);
    }

    @GetMapping("/name/{nickname}/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String nickname,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.findPostsByUser(userId, nickname, pageable);
    }

    @PostMapping(value = "/name/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createBlock(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.createBlock(userId, blockNickname);
    }

    @DeleteMapping(value = "/name/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBlock(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.deleteBlock(userId, blockNickname);
    }

    @GetMapping("/block")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<BlockUserFindResponseDto> findBlockUser(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.blockUserService.findBlockUser(userId, pageable);
    }

    @DeleteMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(
            @RequestHeader(value = "X-USER-ID") String userId
    ) {
//        this.userService.signOut(this.headerUtil.resolveToken(request));
        this.userService.delete(userId);
    }

    @GetMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto retrieveMyAccount(
            @RequestHeader(value = "X-USER-ID") String userId
    ) {
        return this.userService.retrieveMyAccount(userId);
    }

    @GetMapping(value = "/{nickname}/history/centers/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HistoryGroupByMonthDto> findHistoryByCenter(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String nickname,
            @PathVariable String centerId
    ) {
        return this.userService.findHistoryByCenterIdAndUserId(userId, nickname, centerId);
    }

    @GetMapping(value = "/{nickname}/history/centers")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserCenterResponseDto> getCenterHistory(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.userService.findCenterHistory(userId, nickname, pageable);
    }

    @GetMapping(value = "/{nickname}/history")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HistoryByDateFindResponseDto> findHistoryByDate(
            @RequestHeader(value = "X-USER-ID") String userId,
            @PathVariable String nickname,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return this.userService.findHistoryByDateAndUserId(userId, nickname, year, month);
    }
}


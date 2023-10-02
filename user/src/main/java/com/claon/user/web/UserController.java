package com.claon.user.web;

import com.claon.user.common.annotation.RequestUser;
import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.dto.*;
import com.claon.user.dto.request.UserModifyRequestDto;
import com.claon.user.service.BlockUserService;
import com.claon.user.service.UserService;
import com.claon.user.service.client.dto.PostThumbnailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final BlockUserService blockUserService;

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailResponseDto retrieveMe(@RequestUser RequestUserInfo userInfo) {
        return userService.retrieveMe(userInfo);
    }

    @GetMapping("/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPreviewResponseDto> searchUser(
            @RequestUser RequestUserInfo userInfo,
            @RequestParam String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.searchUser(userInfo, nickname, pageable);
    }

    @PutMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto modifyUser(
            @RequestUser RequestUserInfo userInfo,
            @RequestBody @Valid UserModifyRequestDto dto
    ) {
        return userService.modifyUser(userInfo, dto);
    }

    @GetMapping("/name/{targetId}")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailResponseDto getPublicUser(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String targetId
    ) {
        return userService.getOtherUserInformation(userInfo, targetId);
    }

    @GetMapping("/name/{targetId}/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<PostThumbnailResponse> findPostsByUser(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String targetId,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.findPostsByUser(userInfo, targetId, pageable);
    }

    @PostMapping(value = "/name/{blockId}/block")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createBlock(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String blockId
    ) {
        this.blockUserService.createBlock(userInfo, blockId);
    }

    @DeleteMapping(value = "/name/{blockId}/block")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBlock(
            @RequestUser RequestUserInfo userInfo,
            @PathVariable String blockId
    ) {
        this.blockUserService.deleteBlock(userInfo, blockId);
    }

    @GetMapping("/block")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<BlockUserResponseDto> findBlockUser(
            @RequestUser RequestUserInfo userInfo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.blockUserService.findBlockUser(userInfo, pageable);
    }

    @DeleteMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(
            @RequestUser RequestUserInfo userInfo
    ) {
        this.userService.delete(userInfo);
    }

    @GetMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto retrieveMyAccount(
            @RequestUser RequestUserInfo userInfo
    ) {
        return this.userService.retrieveMyAccount(userInfo);
    }
}


package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.dto.BlockUserFindResponseDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/public-scope")
    @ResponseStatus(value = HttpStatus.OK)
    public PublicScopeResponseDto changePublicScope(
            @AuthenticationPrincipal String userId
    ) {
        return this.userService.changePublicScope(userId);
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto getUser(@AuthenticationPrincipal String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto modifyUser(@AuthenticationPrincipal String userId, @RequestBody UserModifyRequestDto dto) {
        return userService.modifyUser(userId, dto);
    }

    @PostMapping(value = "/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createBlock(
            @AuthenticationPrincipal String userId,
            @PathVariable String blockNickname
    ) {
        this.userService.createBlock(userId, blockNickname);
    }

    @DeleteMapping(value = "/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBlock(
            @AuthenticationPrincipal String userId,
            @PathVariable String blockNickname
    ) {
        this.userService.deleteBlock(userId, blockNickname);
    }

    @GetMapping("/{userId}/blocked-users")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<BlockUserFindResponseDto> findBlockUser(
            @AuthenticationPrincipal String userId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 20) final Pageable pageable
    ) {
        return this.userService.findBlockUser(userId, pageable);
    }
}

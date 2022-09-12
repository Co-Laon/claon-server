package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.config.UserAccount;
import coLaon.ClaonBack.user.dto.BlockUserFindResponseDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.IndividualUserResponseDto;
import coLaon.ClaonBack.user.service.BlockUserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final BlockUserService blockUserService;

    @PutMapping("/public-scope")
    @ResponseStatus(value = HttpStatus.OK)
    public PublicScopeResponseDto changePublicScope(
            @AuthenticationPrincipal UserAccount userAccount
    ) {
        return this.userService.changePublicScope(userAccount.getUser());
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto getUser(@AuthenticationPrincipal UserAccount userAccount) {
        return userService.getUser(userAccount.getUser());
    }

    @PutMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto modifyUser(
            @AuthenticationPrincipal UserAccount userAccount,
            @RequestBody UserModifyRequestDto dto
    ) {
        return userService.modifyUser(userAccount.getUser(), dto);
    }

    @GetMapping("/{userNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public IndividualUserResponseDto getPublicUser(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String userNickname
    ) {
        return userService.getOtherUserInformation(userAccount.getUser(), userNickname);
    }

    @PostMapping(value = "/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createBlock(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.createBlock(userAccount.getUser(), blockNickname);
    }

    @DeleteMapping(value = "/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBlock(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.deleteBlock(userAccount.getUser(), blockNickname);
    }

    @GetMapping("/blocked-users")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<BlockUserFindResponseDto> findBlockUser(
            @AuthenticationPrincipal UserAccount userAccount,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 20) final Pageable pageable
    ) {
        return this.blockUserService.findBlockUser(userAccount.getUser(), pageable);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(
            @AuthenticationPrincipal UserAccount userAccount
    ) {
        this.userService.delete(userAccount.getUser());
    }
}

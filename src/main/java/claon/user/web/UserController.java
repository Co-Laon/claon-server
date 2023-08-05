package claon.user.web;

import claon.common.domain.Pagination;
import claon.common.utils.HeaderUtil;
import claon.user.service.BlockUserService;
import claon.user.domain.UserDetails;
import claon.user.dto.BlockUserFindResponseDto;
import claon.user.dto.HistoryByDateFindResponseDto;
import claon.user.dto.HistoryGroupByMonthDto;
import claon.user.dto.IndividualUserResponseDto;
import claon.user.dto.PublicScopeResponseDto;
import claon.user.dto.UserDetailResponseDto;
import claon.user.dto.UserModifyRequestDto;
import claon.user.dto.UserPostThumbnailResponseDto;
import claon.user.dto.UserPreviewResponseDto;
import claon.user.dto.UserResponseDto;
import claon.user.dto.UserCenterResponseDto;
import claon.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final BlockUserService blockUserService;
    private final HeaderUtil headerUtil;

    @PutMapping("/me/scope")
    @ResponseStatus(value = HttpStatus.OK)
    public PublicScopeResponseDto changePublicScope(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return this.userService.changePublicScope(userDetails.getUser());
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailResponseDto retrieveMe(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.retrieveMe(userDetails.getUser());
    }

    @GetMapping("/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPreviewResponseDto> searchUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.searchUser(userDetails.getUser(), nickname, pageable);
    }

    @PutMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto modifyUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UserModifyRequestDto dto
    ) {
        return userService.modifyUser(userDetails.getUser(), dto);
    }

    @GetMapping("/name/{userNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public IndividualUserResponseDto getPublicUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String userNickname
    ) {
        return userService.getOtherUserInformation(userDetails.getUser(), userNickname);
    }

    @GetMapping("/name/{nickname}/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickname,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.findPostsByUser(userDetails.getUser(), nickname, pageable);
    }

    @PostMapping(value = "/name/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createBlock(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.createBlock(userDetails.getUser(), blockNickname);
    }

    @DeleteMapping(value = "/name/{blockNickname}/block")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBlock(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String blockNickname
    ) {
        this.blockUserService.deleteBlock(userDetails.getUser(), blockNickname);
    }

    @GetMapping("/block")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<BlockUserFindResponseDto> findBlockUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.blockUserService.findBlockUser(userDetails.getUser(), pageable);
    }

    @DeleteMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.userService.signOut(this.headerUtil.resolveToken(request));
        this.userService.delete(userDetails.getUser());
    }

    @GetMapping("/me/account")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponseDto retrieveMyAccount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return this.userService.retrieveMyAccount(userDetails.getUser());
    }

    @PostMapping("/me/profile")
    @ResponseStatus(value = HttpStatus.OK)
    public String uploadProfile(
            @RequestPart MultipartFile image
    ) {
        return this.userService.uploadProfile(image);
    }

    @DeleteMapping("/me/profile")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.userService.deleteProfile(userDetails.getUser());
    }

    @GetMapping(value = "/{nickname}/history/centers/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HistoryGroupByMonthDto> findHistoryByCenter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickname,
            @PathVariable String centerId
    ) {
        return this.userService.findHistoryByCenterIdAndUserId(userDetails.getUser(), nickname, centerId);
    }

    @GetMapping(value = "/{nickname}/history/centers")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserCenterResponseDto> getCenterHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.userService.findCenterHistory(userDetails.getUser(), nickname, pageable);
    }

    @GetMapping(value = "/{nickname}/history")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HistoryByDateFindResponseDto> findHistoryByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickname,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return this.userService.findHistoryByDateAndUserId(userDetails.getUser(), nickname, year, month);
    }
}

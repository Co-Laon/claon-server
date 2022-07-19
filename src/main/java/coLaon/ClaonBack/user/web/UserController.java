package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/public-scope")
    @ResponseStatus(value = HttpStatus.OK)
    public PublicScopeResponseDto setPublicScope(
            @AuthenticationPrincipal String userId
    ) {
        return this.userService.setPublicScope(userId);
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
}

package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.config.UserAccount;
import coLaon.ClaonBack.user.dto.LaonFindResponseDto;
import coLaon.ClaonBack.user.service.LaonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private final LaonService laonService;

    @PostMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createLaon(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String laonNickname
    ) {
        this.laonService.createLaon(userAccount.getUser(), laonNickname);
    }

    @DeleteMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLaon(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String laonNickname
    ) {
        this.laonService.deleteLaon(userAccount.getUser(), laonNickname);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LaonFindResponseDto> findAllLaon(
            @AuthenticationPrincipal UserAccount userAccount,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC) @PageableDefault(size = 20) final Pageable pageable
    ) {
        return this.laonService.findAllLaon(userAccount.getUser(), pageable);
    }
}

package coLaon.ClaonBack.config;

import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String userEmail = oAuth2User.getAttribute("email");

        // Find user
        // or else Sign up
        User user = this.userRepository.findByEmail(userEmail)
                .orElse(this.userRepository.save(User.of(userEmail)));

        Boolean isSignUp = Optional.ofNullable(user.getNickname()).isPresent();

        this.jwtUtil.createToken(response, user.getId(), isSignUp);
    }
}

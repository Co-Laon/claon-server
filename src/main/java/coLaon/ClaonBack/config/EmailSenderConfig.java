package coLaon.ClaonBack.config;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties("emailsender-auth")
public class EmailSenderConfig {

    private final Optional<JavaMailSender> emailSender;
    
    private String fromAddress;
}

package coLaon.ClaonBack.config;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Getter
public class UserAccount extends User {
    coLaon.ClaonBack.user.domain.User user;

    public UserAccount(coLaon.ClaonBack.user.domain.User user) {
        super(user.getId(), user.getEmail(), new ArrayList<>());
        this.user = user;
    }
}

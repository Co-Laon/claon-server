package coLaon.ClaonBack.user.domain;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Getter
public class UserDetails extends User {
    coLaon.ClaonBack.user.domain.User user;

    public UserDetails(coLaon.ClaonBack.user.domain.User user) {
        super(user.getId(), user.getEmail(), new ArrayList<>());
        this.user = user;
    }
}

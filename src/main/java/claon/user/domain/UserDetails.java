package claon.user.domain;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Getter
public class UserDetails extends User {
    claon.user.domain.User user;

    public UserDetails(claon.user.domain.User user) {
        super(user.getId(), user.getEmail(), new ArrayList<>());
        this.user = user;
    }
}

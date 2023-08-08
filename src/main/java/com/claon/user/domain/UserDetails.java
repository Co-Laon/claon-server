package com.claon.user.domain;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Getter
public class UserDetails extends User {
    com.claon.user.domain.User user;

    public UserDetails(com.claon.user.domain.User user) {
        super(user.getId(), user.getEmail(), new ArrayList<>());
        this.user = user;
    }
}

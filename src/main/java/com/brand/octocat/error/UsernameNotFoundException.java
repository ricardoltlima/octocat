package com.brand.octocat.error;

import lombok.Getter;

@Getter
public class UsernameNotFoundException extends RuntimeException {

    private final String username;

    public UsernameNotFoundException(String username) {
        super("GitHub user not found: " + username);
        this.username = username;
    }
}



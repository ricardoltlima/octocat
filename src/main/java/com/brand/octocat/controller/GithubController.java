package com.brand.octocat.controller;

import com.brand.octocat.model.dto.UserDto;
import com.brand.octocat.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService service;

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {

        if (StringUtils.isBlank(username)) {
            log.error("Request received with empty username");
            throw new IllegalArgumentException("Username must not be empty");
        }

        log.info("Incoming request for GitHub user '{}'", username);
        UserDto userDto = service.getUser(username);
        return ResponseEntity.ok(userDto);
    }
}

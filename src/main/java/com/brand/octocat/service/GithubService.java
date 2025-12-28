package com.brand.octocat.service;

import com.brand.octocat.config.GithubClient;
import com.brand.octocat.mapper.GithubMapper;
import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import com.brand.octocat.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubClient client;
    private final GithubMapper mapper;

    @Cacheable("users")
    public UserDto getUser(String username) {
        log.info("Fetching user data for '{}'", username);

        GithubUserResponse user = client.getUser(username);
        List<GithubRepoResponse> repos = client.getRepos(username);

        UserDto userDto = mapper.toUserDto(user, repos);

        log.info("Successfully built DTO for '{}'", username);
        return userDto;
    }
}

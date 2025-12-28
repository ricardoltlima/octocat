package com.brand.octocat.config;

import com.brand.octocat.error.UsernameNotFoundException;
import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubClient {

    private final RestTemplate restTemplate;

    @Value("${github.user-path}")
    private String userPath;

    @Value("${github.repos-path}")
    private String reposPath;

    public GithubUserResponse getUser(String username) {
        log.info("Calling GitHub API for user '{}'", username);

        try {
            GithubUserResponse response = restTemplate.getForObject(userPath, GithubUserResponse.class, username);
            log.debug("GitHub user response received for '{}': {}", username, response);
            return response;

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("GitHub user not found. Path='{}', username='{}'.", userPath, username, ex);
            throw new UsernameNotFoundException(username);

        } catch (RestClientException ex) {
            log.error("Error calling GitHub for user '{}'", username, ex);
            throw ex;
        }
    }

    public List<GithubRepoResponse> getRepos(String username) {
        log.info("Calling GitHub API for repos of '{}'", username);

        GithubRepoResponse[] repos = restTemplate.getForObject(reposPath, GithubRepoResponse[].class,username);
        List<GithubRepoResponse> list =
                Optional.ofNullable(repos)
                        .map(Arrays::asList)
                        .orElseGet(List::of);

        log.debug("GitHub repo count for '{}': {}", username, list.size());
        return list;
    }
}


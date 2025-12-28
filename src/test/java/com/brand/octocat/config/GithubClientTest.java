package com.brand.octocat.config;

import com.brand.octocat.error.UsernameNotFoundException;
import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GithubClient githubClient;

    @BeforeEach
    void setup() {
        // simulate @Value injection
        ReflectionTestUtils.setField(githubClient, "userPath", "/users/{username}");
        ReflectionTestUtils.setField(githubClient, "reposPath", "/users/{username}/repos");
    }

    @Test
    void getUser_shouldReturnUser_whenGithubRespondsOk() {
        String username = "octocat";

        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                "The Octocat",
                "avatar-url",
                "San Francisco",
                "octo@cat.com",
                "https://api.github.com/users/octocat",
                OffsetDateTime.parse("2011-01-25T18:44:36Z")
        );

        when(restTemplate
                .getForObject("/users/{username}", GithubUserResponse.class, username))
                .thenReturn(user);

        GithubUserResponse result = githubClient.getUser(username);

        assertThat(result).isSameAs(user);
    }

    @Test
    void getUser_shouldThrowUsernameNotFound_whenGithubReturns404() {
        String username = "missing";

        when(restTemplate.getForObject("/users/{username}", GithubUserResponse.class, username))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, null, null, null, null));

        assertThatThrownBy(() -> githubClient.getUser(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("GitHub user not found: missing");
    }

    @Test
    void getUser_shouldRethrowRestClientException_whenOtherClientErrorOccurs() {
        String username = "octocat";

        when(restTemplate.getForObject("/users/{username}", GithubUserResponse.class, username))
                .thenThrow(new RestClientException("GitHub timeout"));

        assertThatThrownBy(() -> githubClient.getUser(username))
                .isInstanceOf(RestClientException.class)
                .hasMessage("GitHub timeout");
    }

    @Test
    void getRepos_shouldReturnMappedList_whenGithubReturnsArray() {
        String username = "octocat";

        GithubRepoResponse[] reposArray = new GithubRepoResponse[] {
                new GithubRepoResponse("repo-1", "url-1"),
                new GithubRepoResponse("repo-2", "url-2")
        };

        when(restTemplate.getForObject("/users/{username}/repos", GithubRepoResponse[].class, username))
                .thenReturn(reposArray);

        List<GithubRepoResponse> result = githubClient.getRepos(username);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("repo-1");
        assertThat(result.get(1).name()).isEqualTo("repo-2");
    }

    @Test
    void getRepos_shouldReturnEmptyList_whenGithubReturnsNullBody() {
        String username = "octocat";

        when(restTemplate
                .getForObject("/users/{username}/repos", GithubRepoResponse[].class, username))
                .thenReturn(null);

        List<GithubRepoResponse> result = githubClient.getRepos(username);

        assertThat(result).isEmpty();
    }
}

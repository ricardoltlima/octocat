package com.brand.octocat.service;


import com.brand.octocat.config.GithubClient;
import com.brand.octocat.mapper.GithubMapper;
import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import com.brand.octocat.model.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    private GithubClient client;

    @Mock
    private GithubMapper mapper;

    @InjectMocks
    private GithubService service;

    @Test
    void getUser_shouldFetchUserAndReposAndMapToDto() {
        // arrange
        String username = "octocat";

        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                "The Octocat",
                "avatar-url",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                OffsetDateTime.parse("2011-01-25T18:44:36Z")
        );

        List<GithubRepoResponse> repos = List.of(
                new GithubRepoResponse(
                        "repo-1",
                        "https://api.github.com/repos/octocat/repo-1"
                ),
                new GithubRepoResponse(
                        "repo-2",
                        "https://api.github.com/repos/octocat/repo-2"
                )
        );

        UserDto expectedDto = new UserDto(
                "octocat",
                "The Octocat",
                "avatar-url",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                "Tue, 25 Jan 2011 18:44:36 GMT",
                List.of() // or mapped repos, but for this test it's enough
        );

        when(client.getUser(username)).thenReturn(user);
        when(client.getRepos(username)).thenReturn(repos);
        when(mapper.toUserDto(user, repos)).thenReturn(expectedDto);

        // act
        UserDto result = service.getUser(username);

        // assert
        assertThat(result).isSameAs(expectedDto);

        verify(client).getUser(username);
        verify(client).getRepos(username);
        verify(mapper).toUserDto(user, repos);

        verifyNoMoreInteractions(client, mapper);
    }
}

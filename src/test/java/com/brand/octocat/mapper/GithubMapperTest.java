package com.brand.octocat.mapper;

import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import com.brand.octocat.model.dto.RepoDto;
import com.brand.octocat.model.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubMapperTest {

    private final GithubMapper mapper = Mappers.getMapper(GithubMapper.class);

    @Test
    void toUserDto_shouldMapAllFieldsAndFormatDate_andMapRepos() {
        // given
        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                "The Octocat",
                "avatar-url",
                "San Francisco",
                "octo@cat.com",
                "https://api.github.com/users/octocat",
                OffsetDateTime.parse("2011-01-25T18:44:36Z")
        );

        List<GithubRepoResponse> repos = List.of(
                new GithubRepoResponse("repo-1", "https://api.github.com/repos/octocat/repo-1"),
                new GithubRepoResponse("repo-2", "https://api.github.com/repos/octocat/repo-2")
        );

        // when
        UserDto dto = mapper.toUserDto(user, repos);

        // then - basic fields
        assertThat(dto.userName()).isEqualTo("octocat");
        assertThat(dto.displayName()).isEqualTo("The Octocat");
        assertThat(dto.avatar()).isEqualTo("avatar-url");
        assertThat(dto.geoLocation()).isEqualTo("San Francisco");
        assertThat(dto.email()).isEqualTo("octo@cat.com");
        assertThat(dto.url()).isEqualTo("https://api.github.com/users/octocat");

        // then - date formatting (RFC_1123)
        assertThat(dto.createdAt())
                .isEqualTo("Tue, 25 Jan 2011 18:44:36 GMT");

        // then - repos mapping
        assertThat(dto.repos()).hasSize(2);
        assertThat(dto.repos())
                .extracting(RepoDto::name)
                .containsExactly("repo-1", "repo-2");
        assertThat(dto.repos())
                .extracting(RepoDto::url)
                .containsExactly("https://api.github.com/repos/octocat/repo-1", "https://api.github.com/repos/octocat/repo-2");
    }

    @Test
    void toUserDto_shouldSetCreatedAtNull_whenSourceCreatedAtIsNull() {
        // given
        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                "The Octocat",
                "avatar-url",
                "SF",
                null,
                "https://api.github.com/users/octocat",
                null // createdAt is null
        );

        // when
        UserDto dto = mapper.toUserDto(user, List.of());

        // then
        assertThat(dto.createdAt()).isNull();
        assertThat(dto.repos()).isEmpty();
    }

    @Test
    void toUserDto_shouldHandleEmptyReposList() {
        // given
        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                "The Octocat",
                "avatar-url",
                "SF",
                null,
                "url",
                OffsetDateTime.of(2011, 1, 25, 18, 44, 36, 0, ZoneOffset.UTC)
        );

        // when
        UserDto dto = mapper.toUserDto(user, List.of());

        // then
        assertThat(dto.repos()).isEmpty();
    }

    @Test
    void toUserDto_shouldSetReposNull_whenSourceReposIsNull() {
        // given
        GithubUserResponse user = new GithubUserResponse(
                "octocat",
                null,
                null,
                null,
                null,
                "url",
                OffsetDateTime.of(2011, 1, 25, 18, 44, 36, 0, ZoneOffset.UTC)
        );

        // when
        UserDto dto = mapper.toUserDto(user, null);

        // then
        assertThat(dto.userName()).isEqualTo("octocat");
        assertThat(dto.displayName()).isNull();
        assertThat(dto.avatar()).isNull();
        assertThat(dto.geoLocation()).isNull();
        assertThat(dto.email()).isNull();
        assertThat(dto.repos()).isNull(); // MapStruct default behavior for null list
    }

    @Test
    void toRepoDto_shouldMapFieldsCorrectly() {
        // given
        GithubRepoResponse repo = new GithubRepoResponse(
                "demo-repo",
                "https://api.github.com/repos/octocat/demo-repo"
        );

        // when
        RepoDto dto = mapper.toRepoDto(repo);

        // then
        assertThat(dto.name()).isEqualTo("demo-repo");
        assertThat(dto.url()).isEqualTo("https://api.github.com/repos/octocat/demo-repo");
    }

    @Test
    void formatCreatedAt_shouldReturnRfc1123String() {
        // given
        OffsetDateTime createdAt = OffsetDateTime.of(2011, 1, 25, 18, 44, 36, 0, ZoneOffset.UTC);

        // when
        String formatted = mapper.formatCreatedAt(createdAt);

        // then
        assertThat(formatted).isEqualTo("Tue, 25 Jan 2011 18:44:36 GMT");
    }

    @Test
    void toUserDto_shouldReturnDtoWithNullFields_whenUserAndReposAreNull() {
        // when
        UserDto dto = mapper.toUserDto(null, null);

        // then
        assertThat(dto).isNull();
    }


    @Test
    void formatCreatedAt_shouldReturnNull_whenInputIsNull() {
        // when
        String formatted = mapper.formatCreatedAt(null);

        // then
        assertThat(formatted).isNull();
    }
}

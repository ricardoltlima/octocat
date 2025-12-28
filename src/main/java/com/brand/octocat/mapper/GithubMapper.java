package com.brand.octocat.mapper;

import com.brand.octocat.model.api.GithubRepoResponse;
import com.brand.octocat.model.api.GithubUserResponse;
import com.brand.octocat.model.dto.RepoDto;
import com.brand.octocat.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GithubMapper {

    @Mapping(target = "userName", source = "user.login")
    @Mapping(target = "displayName", source = "user.name")
    @Mapping(target = "avatar", source = "user.avatarUrl")
    @Mapping(target = "geoLocation", source = "user.location")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "url", source = "user.url")
    @Mapping(target = "createdAt", expression = "java(formatCreatedAt(user.createdAt()))")
    @Mapping(target = "repos", source = "repos")
    UserDto toUserDto(GithubUserResponse user, List<GithubRepoResponse> repos);

    RepoDto toRepoDto(GithubRepoResponse repo);

    default String formatCreatedAt(OffsetDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(createdAt);
    }
}


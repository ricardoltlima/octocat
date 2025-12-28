package com.brand.octocat.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record GithubUserResponse(
        String login,
        String name,
        @JsonProperty("avatar_url") String avatarUrl,
        String location,
        String email,
        String url,
        @JsonProperty("created_at") OffsetDateTime createdAt) {
}

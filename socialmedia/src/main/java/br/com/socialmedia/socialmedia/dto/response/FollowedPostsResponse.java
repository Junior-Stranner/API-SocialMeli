package br.com.socialmedia.socialmedia.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FollowedPostsResponse(
        @JsonProperty("user_id")
        long userId,
        List<PostResponse> posts
) {}
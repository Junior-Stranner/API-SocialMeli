package br.com.socialmedia.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowDto(
        @JsonProperty("user_id") int userId,
        @JsonProperty("user_name") String userName
) {}
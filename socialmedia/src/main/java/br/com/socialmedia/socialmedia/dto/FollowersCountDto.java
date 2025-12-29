package br.com.socialmedia.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowersCountDto(
        @JsonProperty("user_id") Integer userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("followers_count") Integer followersCount
) { }
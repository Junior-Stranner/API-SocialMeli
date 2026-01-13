package br.com.socialmedia.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FollowersListDto(
        @JsonProperty("user_id") long userId,
        @JsonProperty("user_name") String userName,
        List<FollowDto> followers
) { }
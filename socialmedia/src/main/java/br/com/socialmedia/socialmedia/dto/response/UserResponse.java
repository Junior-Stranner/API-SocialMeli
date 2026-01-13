package br.com.socialmedia.socialmedia.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        @JsonProperty("user_id") long userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("is_seller") boolean seller
) {}

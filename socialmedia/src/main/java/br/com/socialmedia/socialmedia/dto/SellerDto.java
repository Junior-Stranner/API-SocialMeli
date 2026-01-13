package br.com.socialmedia.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SellerDto(
        @JsonProperty("user_id") long userId,
        @JsonProperty("user_name") String userName
) {}
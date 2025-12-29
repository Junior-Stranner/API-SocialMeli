package br.com.socialmedia.socialmedia.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PromoPostsResponse(
        @JsonProperty("user_id") int userId,
        @JsonProperty("user_name") String userName,
        List<PostResponse> posts
) {}

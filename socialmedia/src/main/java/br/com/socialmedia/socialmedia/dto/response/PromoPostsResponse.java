package br.com.socialmedia.socialmedia.dto.response;


import java.util.List;

public record PromoPostsResponse(
        int userId,
        String userName,
        List<PostResponse> posts
) {}

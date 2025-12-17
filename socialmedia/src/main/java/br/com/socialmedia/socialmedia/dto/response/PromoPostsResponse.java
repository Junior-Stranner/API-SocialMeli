package br.com.socialmedia.socialmedia.dto.response;


import java.util.List;

public record PromoPostsResponse(
        int user_id,
        String user_name,
        List<PostResponse> posts
) {}

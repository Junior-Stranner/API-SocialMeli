package br.com.socialmedia.socialmedia.dto.response;

import java.util.List;

public record FollowedPostsResponse(
        int user_id,
        List<PostResponse> posts
) {}
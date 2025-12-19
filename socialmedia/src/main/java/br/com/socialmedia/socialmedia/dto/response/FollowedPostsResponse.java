package br.com.socialmedia.socialmedia.dto.response;

import java.util.List;

public record FollowedPostsResponse(
        int userId,
        List<PostResponse> posts
) {}
package br.com.socialmedia.socialmedia.dto;

import java.util.List;

public record FollowedListDto(
        int userId,
        String userName,
        List<FollowDto> followed
) {}

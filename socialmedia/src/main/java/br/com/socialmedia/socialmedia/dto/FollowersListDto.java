package br.com.socialmedia.socialmedia.dto;

import java.util.List;

public record FollowersListDto(
        Integer userId,
        String userName,
        List<FollowDto> followers
) { }

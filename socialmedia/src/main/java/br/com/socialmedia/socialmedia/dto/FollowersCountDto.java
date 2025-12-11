package br.com.socialmedia.socialmedia.dto;

public record FollowersCountDto(
        Integer userId,
        String userName,
        Integer followersCount
) { }

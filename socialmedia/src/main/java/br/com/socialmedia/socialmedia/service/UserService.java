package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse follow(int userId, int userIdToFollow);
    UserResponse unfollow(int userId, int userIdToUnfollow);

    FollowersCountDto getFollowersCount(int userId);

    FollowersListDto getFollowersList(int userId, String order);
    FollowedListDto getFollowedList(int userId, String order);

    // Se quiser manter os "extras"
    List<UserResponse> getFollowers(int userId, String order);
    List<UserResponse> getFollowed(int userId, String order);
}
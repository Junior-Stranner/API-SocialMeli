package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse follow(int userId, int userIdToFollow);
    UserResponse unfollow(int userId, int userIdToUnfollow);

    List<UserResponse> getFollowers(int userId, String order);
    List<UserResponse> getFollowed(int userId, String order);

    FollowersCountDto getFollowersCount(int userId);
    FollowersListDto getFollowersList(int userId);
}

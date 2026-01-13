package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;

public interface IUserService {

    UserResponse follow(long userId, long userIdToFollow);

    UserResponse unfollow(long userId, long userIdToUnfollow);

    FollowersCountDto getFollowersCount(long userId);

    FollowersListDto getFollowersList(long userId, String order);

    FollowedListDto getFollowedList(long userId, String order);
}
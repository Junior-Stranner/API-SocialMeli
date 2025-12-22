package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.exception.ConflictException;

import java.util.List;

public interface IUserService {
    UserResponse follow(int userId, int userIdToFollow) throws ConflictException;
    UserResponse unfollow(int userId, int userIdToUnfollow) throws ConflictException;

    FollowersCountDto getFollowersCount(int userId);

    FollowersListDto getFollowersList(int userId, String order);
    FollowedListDto getFollowedList(int userId, String order);


}
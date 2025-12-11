package br.com.socialmedia.socialmedia.service.serviceImpl;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void follow(int userId, int userIdToFollow) {
        if (userId == userIdToFollow) {
            throw new BusinessException("User cannot follow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        User seller = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userIdToFollow + " not found"));

        if (!seller.isSeller()) {
            throw new BusinessException("User " + userIdToFollow + " is not a seller");
        }

        if (user.isFollowing(seller)) {
            throw new BusinessException("User " + userId + " is already following user " + userIdToFollow);
        }

        seller.addFollower(user);
        userRepository.save(seller);
    }

    @Override
    public void unfollow(int userId, int userIdToUnfollow) {
        if (userId == userIdToUnfollow) {
            throw new BusinessException("User cannot unfollow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        User seller = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userIdToUnfollow + " not found"));

        if (!user.isFollowing(seller)) {
            throw new BusinessException("User " + userId + " is not following user " + userIdToUnfollow);
        }

        seller.removeFollower(user);
        userRepository.save(seller);
    }

    @Override
    public List<UserResponse> getFollowers(int userId, String order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> followed = new ArrayList<>(user.getFollowers());

        sortByName(followed, order);

        return followed.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getFollowed(int userId, String order) {
        return null;
    }

    @Override
    public FollowersCountDto getFollowersCount(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        int count = user.getFollowersCount();
        return new FollowersCountDto(
                user.getId(),
                user.getName(),
                count
        );
    }

    @Override
    public FollowersListDto getFollowersList(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> followers = new ArrayList<>(user.getFollowing());

        List<FollowDto> followingDto = userMapper.toFollowList(followers);


        return null;
    }

    private void sortByName(List<User> users, String order){
        if(order == null || order.isBlank() || order.equalsIgnoreCase("name_asc")){
            users.sort(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER));
        }else if(order.equalsIgnoreCase("name_desc")){
            users.sort(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER.reversed());
        }
    }
}

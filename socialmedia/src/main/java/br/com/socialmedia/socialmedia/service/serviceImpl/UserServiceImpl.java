package br.com.socialmedia.socialmedia.service.serviceImpl;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse follow(int userId, int userIdToFollow) {
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

        if (user.isSeller()) {
            throw new BusinessException("Seller cannot follow another seller");
        }

        if (user.isFollowing(seller)) {
            throw new ConflictException("User " + userId + " is already following user " + userIdToFollow);
        }

        seller.addFollower(user);
        userRepository.save(seller);

        return userMapper.toDto(seller);
    }

    @Transactional
    @Override
    public UserResponse unfollow(int userId, int userIdToUnfollow) {
        if (userId == userIdToUnfollow) {
            throw new BusinessException("User cannot unfollow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        User seller = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userIdToUnfollow + " not found"));

        if (!seller.isSeller()) {
            throw new BusinessException("User " + userIdToUnfollow + " is not a seller");
        }

        if (user.isSeller()) {
            throw new BusinessException("Seller cannot unfollow another seller");
        }

        if (!user.isFollowing(seller)) {
            throw new ConflictException("User " + userId + " is not following user " + userIdToUnfollow);
        }

        seller.removeFollower(user);
        userRepository.save(seller);

        return userMapper.toDto(seller);
    }

    @Override
    public FollowersCountDto getFollowersCount(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        int count = userRepository.countFollowers(userId);
        return new FollowersCountDto(user.getId(), user.getName(), count);
    }

    @Override
    public FollowersListDto getFollowersList(int userId, String order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> followers = switch (normalize(order, "name_asc")) {
            case "name_desc" -> userRepository.findFollowersOrderByNameDesc(userId);
            case "name_asc"  -> userRepository.findFollowersOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };

        List<FollowDto> followersDto = userMapper.toFollowList(followers);
        return new FollowersListDto(userId, user.getName(), followersDto);
    }

    @Override
    public FollowedListDto getFollowedList(int userId, String order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> followed = switch (normalize(order, "name_asc")) {
            case "name_desc" -> userRepository.findFollowedOrderByNameDesc(userId);
            case "name_asc"  -> userRepository.findFollowedOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };

        List<FollowDto> followedDto = userMapper.toFollowList(followed);
        return new FollowedListDto(userId, user.getName(), followedDto);
    }

    private String normalize(String order, String defaultValue) {
        if (order == null || order.isBlank()) return defaultValue;
        return order.toLowerCase();
    }
}
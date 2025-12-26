package br.com.socialmedia.socialmedia.service.serviceImpl;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserFollowRepository followRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse follow(int userId, int userIdToFollow) {
        if (userId == userIdToFollow) {
            throw new BusinessException("User cannot follow themselves");
        }

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        User seller = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userIdToFollow + " not found"));

        validateFollow(buyer, seller);

        followRepository.save(new UserFollow(buyer, seller));
        return userMapper.toDto(seller);
    }

    @Transactional
    @Override
    public UserResponse unfollow(int userId, int userIdToUnfollow) {
        if (userId == userIdToUnfollow) {
            throw new BusinessException("User cannot unfollow themselves");
        }

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        User seller = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userIdToUnfollow + " not found"));

        validateUnfollow(buyer, seller);

        followRepository.deleteByFollowerIdAndSellerId(buyer.getId(), seller.getId());
        return userMapper.toDto(seller);
    }

    @Override
    public FollowersCountDto getFollowersCount(int userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getId() + " is not a seller");
        }

        int count = (int) followRepository.countBySellerId(userId);
        return new FollowersCountDto(seller.getId(), seller.getName(), count);
    }

    @Override
    public FollowersListDto getFollowersList(int userId, String order) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getId() + " is not a seller");
        }

        List<User> followers = switch (normalize(order, "name_asc")) {
            case "name_desc" -> followRepository.findFollowersOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowersOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };

        List<FollowDto> followersDto = userMapper.toFollowList(followers);
        return new FollowersListDto(userId, seller.getName(), followersDto);
    }

    @Override
    public FollowedListDto getFollowedList(int userId, String order) {
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (buyer.isSeller()) {
            throw new BusinessException("Seller cannot follow another seller");
        }

        List<User> followed = switch (normalize(order, "name_asc")) {
            case "name_desc" -> followRepository.findFollowedOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowedOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };

        List<FollowDto> followedDto = userMapper.toFollowList(followed);
        return new FollowedListDto(userId, buyer.getName(), followedDto);
    }

    private void validateFollow(User buyer, User seller) {
        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getId() + " is not a seller");
        }
        if (buyer.isSeller()) {
            throw new BusinessException("Seller cannot follow another seller");
        }
        if (followRepository.existsByFollowerIdAndSellerId(buyer.getId(), seller.getId())) {
            throw new ConflictException("User " + buyer.getId() + " is already following user " + seller.getId());
        }
    }

    private void validateUnfollow(User buyer, User seller) {
        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getId() + " is not a seller");
        }
        if (buyer.isSeller()) {
            throw new BusinessException("Seller cannot unfollow another seller");
        }
        if (!followRepository.existsByFollowerIdAndSellerId(buyer.getId(), seller.getId())) {
            throw new ConflictException("User " + buyer.getId() + " is not following user " + seller.getId());
        }
    }

    private String normalize(String order, String defaultValue) {
        if (order == null || order.isBlank()) return defaultValue;
        return order.toLowerCase();
    }
}
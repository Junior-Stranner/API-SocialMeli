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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserFollowRepository followRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse follow(long userId, long userIdToFollow) {
        log.info("User {} try to follow user {}", userId, userIdToFollow);
        validateNotSameUser(userId, userIdToFollow);

        User buyer = findUserById(userId);
        User seller = findUserById(userIdToFollow);

        validateFollow(buyer, seller);

        followRepository.save(new UserFollow(buyer, seller));
        log.info("User {} now follows user {}", userId, userIdToFollow);
        return userMapper.toDto(seller);
    }

    @Transactional
    @Override
    public UserResponse unfollow(long userId, long userIdToUnfollow) {
        log.info("User {} attempting to unfollow user {}", userId, userIdToUnfollow);
        validateNotSameUser(userId, userIdToUnfollow);

        User buyer = findUserById(userId);
        User seller = findUserById(userIdToUnfollow);

        validateUnfollow(buyer, seller);

        followRepository.deleteByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId());
        log.info("User {} unfollowed user {}", userId, userIdToUnfollow);
        return userMapper.toDto(seller);
    }

    @Override
    public FollowersCountDto getFollowersCount(long userId) {
        User seller = findSellerById(userId);
        Integer count = Math.toIntExact(followRepository.countBySellerId(userId));
        log.debug("Seller {} has {} followers", userId, count);
        return new FollowersCountDto(seller.getUserId(), seller.getName(), count);
    }

    @Override
    public FollowersListDto getFollowersList(long sellerId, String order) {
        User seller = findSellerById(sellerId);
        List<User> followers = findFollowersByOrder(sellerId, order);
        List<FollowDto> followersDto = userMapper.toFollowList(followers);
        return new FollowersListDto(sellerId, seller.getName(), followersDto);
    }

    @Override
    public FollowedListDto getFollowedList(long buyerId, String order) {
        User buyer = findBuyerById(buyerId);
        List<User> followed = findFollowedByOrder(buyerId, order);
        List<FollowDto> followedDto = userMapper.toFollowList(followed);
        return new FollowedListDto(buyerId, buyer.getName(), followedDto);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    private User findSellerById(long userId) {
        User user = findUserById(userId);
        if (!user.isSeller()) {
            throw new BusinessException("User " + userId + " is not a seller");
        }
        return user;
    }

    private User findBuyerById(long userId) {
        User user = findUserById(userId);
        if (user.isSeller()) {
            throw new BusinessException("Seller cannot follow another seller");
        }
        return user;
    }

    private List<User> findFollowersByOrder(long userId, String order) {
        return switch (normalizeOrder(order)) {
            case "name_desc" -> followRepository.findFollowersOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowersOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };
    }

    private List<User> findFollowedByOrder(long userId, String order) {
        return switch (normalizeOrder(order)) {
            case "name_desc" -> followRepository.findFollowedOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowedOrderByNameAsc(userId);
            default -> throw new BusinessException("Invalid order param: " + order);
        };
    }

    private void validateNotSameUser(long userId, long targetId) {
        if (userId == targetId) {
            throw new BusinessException("User cannot follow/unfollow themselves");
        }
    }

    private void validateFollow(User buyer, User seller) {
        validateUserRoles(buyer, seller);
        if (followRepository.existsByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId())) {
            throw new ConflictException("User " + buyer.getUserId() + " is already following user " + seller.getUserId());
        }
    }

    private void validateUnfollow(User buyer, User seller) {
        validateUserRoles(buyer, seller);
        if (!followRepository.existsByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId())) {
            throw new ConflictException("User " + buyer.getUserId() + " is not following user " + seller.getUserId());
        }
    }

    private void validateUserRoles(User buyer, User seller) {
        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getUserId() + " is not a seller");
        }
        if (buyer.isSeller()) {
            throw new BusinessException("Seller cannot follow another seller");
        }
    }

    private String normalizeOrder(String order) {
        return (order == null || order.isBlank()) ? "name_asc" : order.toLowerCase();
    }
}
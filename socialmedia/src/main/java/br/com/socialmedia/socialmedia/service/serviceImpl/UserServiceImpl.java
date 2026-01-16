package br.com.socialmedia.socialmedia.service.serviceImpl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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

    @Override
    public UserResponse follow(long userId, long userIdToFollow) {
        log.info("Follow request: followerId={}, targetId={}", userId, userIdToFollow);

        validateNotSameUser(userId, userIdToFollow);

        User buyer = findUserById(userId);
        User seller = findUserById(userIdToFollow);

        validateFollow(buyer, seller);

        followRepository.save(new UserFollow(buyer, seller));

        log.info("Follow created: followerId={}, sellerId={}", buyer.getUserId(), seller.getUserId());
        return userMapper.toDto(seller);
    }

    @Transactional
    @Override
    public UserResponse unfollow(long userId, long userIdToUnfollow) {
        log.info("Unfollow request: followerId={}, targetId={}", userId, userIdToUnfollow);

        validateNotSameUser(userId, userIdToUnfollow);

        User buyer = findUserById(userId);
        User seller = findUserById(userIdToUnfollow);

        validateUnfollow(buyer, seller);

        followRepository.deleteByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId());

        log.info("Follow removed: followerId={}, sellerId={}", buyer.getUserId(), seller.getUserId());
        return userMapper.toDto(seller);
    }

    @Override
    public FollowersCountDto getFollowersCount(long userId) {
        log.debug("Get followers count: sellerId={}", userId);

        User seller = findSellerById(userId);
        int count = Math.toIntExact(followRepository.countBySellerId(userId));

        log.debug("Followers count computed: sellerId={}, count={}", userId, count);
        return new FollowersCountDto(seller.getUserId(), seller.getName(), count);
    }

    @Override
    public FollowersListDto getFollowersList(long sellerId, String order) {
        String normalized = normalizeOrder(order);
        log.debug("Get followers list: sellerId={}, order={}, normalized={}", sellerId, order, normalized);

        User seller = findSellerById(sellerId);
        List<User> followers = findFollowersByOrder(sellerId, normalized);

        log.debug("Followers list loaded: sellerId={}, size={}", sellerId, followers.size());
        return new FollowersListDto(sellerId, seller.getName(), userMapper.toFollowList(followers));
    }

    @Override
    public FollowedListDto getFollowedList(long buyerId, String order) {
        String normalized = normalizeOrder(order);
        log.debug("Get followed list: buyerId={}, order={}, normalized={}", buyerId, order, normalized);

        User buyer = findBuyerById(buyerId);
        List<User> followed = findFollowedByOrder(buyerId, normalized);

        log.debug("Followed list loaded: buyerId={}, size={}", buyerId, followed.size());
        return new FollowedListDto(buyerId, buyer.getName(), userMapper.toFollowList(followed));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User not found: userId={}", userId);
            return new EntityNotFoundException("User with id " + userId + " not found");
        });
    }

    private User findSellerById(long userId) {
        User user = findUserById(userId);
        if (!user.isSeller()) {
            log.warn("Role conflict: userId={} is not a seller", userId);
            throw new BusinessException("User " + userId + " is not a seller");
        }
        return user;
    }

    private User findBuyerById(long userId) {
        User user = findUserById(userId);
        if (user.isSeller()) {
            log.warn("Role conflict: userId={} is seller, expected buyer", userId);
            throw new BusinessException("User " + userId + " is a seller; only buyers can perform this operation");
        }
        return user;
    }

    private List<User> findFollowersByOrder(long userId, String order) {
        return switch (order) {
            case "name_desc" -> followRepository.findFollowersOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowersOrderByNameAsc(userId);
            default -> {
                log.warn("Invalid order param (followers list): sellerId={}, order={}", userId, order);
                throw new BusinessException("Invalid order param: " + order);
            }
        };
    }

    private List<User> findFollowedByOrder(long userId, String order) {
        return switch (order) {
            case "name_desc" -> followRepository.findFollowedOrderByNameDesc(userId);
            case "name_asc" -> followRepository.findFollowedOrderByNameAsc(userId);
            default -> {
                log.warn("Invalid order param (followed list): buyerId={}, order={}", userId, order);
                throw new BusinessException("Invalid order param: " + order);
            }
        };
    }

    private void validateNotSameUser(long userId, long targetId) {
        if (userId == targetId) {
            log.warn("Invalid operation: userId equals targetId (userId={})", userId);
            throw new BusinessException("User cannot follow/unfollow themselves");
        }
    }

    private void validateFollow(User buyer, User seller) {
        validateUserRoles(buyer, seller);

        boolean exists = followRepository.existsByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId());
        if (exists) {
            log.warn("Follow conflict: followerId={} already follows sellerId={}", buyer.getUserId(), seller.getUserId());
            throw new ConflictException("User " + buyer.getUserId() + " is already following user " + seller.getUserId());
        }
    }

    private void validateUnfollow(User buyer, User seller) {
        validateUserRoles(buyer, seller);

        boolean exists = followRepository.existsByFollowerIdAndSellerId(buyer.getUserId(), seller.getUserId());
        if (!exists) {
            log.warn("Unfollow conflict: followerId={} does not follow sellerId={}", buyer.getUserId(), seller.getUserId());
            throw new ConflictException("User " + buyer.getUserId() + " is not following user " + seller.getUserId());
        }
    }

    private void validateUserRoles(User buyer, User seller) {
        if (!seller.isSeller()) {
            log.warn("Role conflict: targetId={} is not seller", seller.getUserId());
            throw new BusinessException("User " + seller.getUserId() + " is not a seller");
        }
        if (buyer.isSeller()) {
            log.warn("Role conflict: followerId={} is seller (sellers cannot follow)", buyer.getUserId());
            throw new BusinessException("Seller cannot follow another seller");
        }
    }

    private String normalizeOrder(String order) {
        return (order == null || order.isBlank()) ? "name_asc" : order.toLowerCase();
    }
}
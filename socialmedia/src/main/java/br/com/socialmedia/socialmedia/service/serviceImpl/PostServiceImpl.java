package br.com.socialmedia.socialmedia.service.serviceImpl;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoCountResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoPostsResponse;
import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.mapper.PostMapper;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.IPostService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class PostServiceImpl implements IPostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    public PostServiceImpl(UserRepository userRepository,
                           UserFollowRepository followRepository,
                           PostMapper postMapper,
                           PostRepository postRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.postMapper = postMapper;
        this.postRepository = postRepository;
    }

    @Override
    public void publish(PostPublishRequest request) {
        log.info("Publish post request: userId={}", request.getUserId());
        User seller = findSellerById(request.getUserId());

        Post entity = postMapper.toEntity(request);
        entity.setPostId(null);
        entity.setUser(seller);
        entity.setHasPromo(false);
        entity.setDiscount(0.0);

        Post saved = postRepository.save(entity);
        log.info("Post published: postId={}, sellerId={}, promo={}", saved.getPostId(), seller.getUserId(), false);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowedPostsResponse getFollowedPostsLastTwoWeeks(long userId, String order) {
        LocalDate since = LocalDate.now().minusWeeks(2);
        log.debug("Get followed posts last two weeks: buyerId={}, order={}, since={}", userId, order, since);
        findBuyerById(userId);

        List<User> followedSellers = followRepository.findFollowedSellers(userId);
        if (followedSellers == null || followedSellers.isEmpty()) {
            log.debug("Buyer {} follows no sellers. Returning empty feed.", userId);
            return new FollowedPostsResponse(userId, List.of());
        }
        log.debug("Buyer {} follows {} sellers", userId, followedSellers.size());

        List<Post> posts = postRepository.findByUserInAndDateAfterOrderByDateDesc(
                new HashSet<>(followedSellers), since);

        log.debug("Posts fetched before sorting: buyerId={}, postsCount={}", userId, posts.size());

        posts = sortPost(posts, order);
        log.debug("Feed ready: buyerId={}, postsCount={}", userId, posts.size());

        return new FollowedPostsResponse(userId, posts.stream().map(postMapper::toDto).toList());
    }

    @Override
    public void publishPromo(PromoPostPublishRequest request) {
        log.info("Publish promo post request: userId={}, discount={}", request.getUserId(), request.getDiscount());

        User seller = findSellerById(request.getUserId());
        validatePromoDiscount(request.getDiscount());

        Post entity = postMapper.toEntity(request);
        entity.setPostId(null);
        entity.setUser(seller);
        entity.setHasPromo(true);
        entity.setDiscount(request.getDiscount());

        Post saved = postRepository.save(entity);
        log.info("Promo post published: postId={}, sellerId={}, discount={}", saved.getPostId(), seller.getUserId(), saved.getDiscount());
    }

    @Override
    public PromoCountResponse getPromoCount(long userId) {
        log.debug("Get promo count: sellerId={}", userId);

        User seller = findSellerById(userId);
        long count = postRepository.countByUserIdAndHasPromoTrue(userId);

        log.debug("Promo count computed: sellerId={}, count={}", userId, count);

        return new PromoCountResponse(userId, seller.getName(), count);
    }

    @Override
    public PromoPostsResponse getPromoPosts(long userId) {
        log.debug("Get promo posts: sellerId={}", userId);

        User seller = findSellerById(userId);
        List<Post> promoPosts = postRepository.findPromoPostsBySellerIdFetchUser(userId);

        log.debug("Promo posts fetched: sellerId={}, size={}", userId, promoPosts.size());

        return new PromoPostsResponse(
                userId,
                seller.getName(),
                promoPosts.stream().map(postMapper::toDto).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PromoPostsResponse getPromoPostsForFollower(long buyerId, long sellerId) {
        log.debug("Get promo posts for follower: buyerId={}, sellerId={}", buyerId, sellerId);
        findBuyerById(buyerId);
        User seller = findSellerById(sellerId);

        boolean follows = followRepository.existsByFollowerIdAndSellerId(buyerId, sellerId);
        if (!follows) {
            throw new BusinessException("Buyer does not follow this seller");
        }

        List<Post> promoPosts = postRepository.findPromoPostsBySellerIdFetchUser(sellerId);
        log.debug("Promo posts returned to follower: buyerId={}, sellerId={}, size={}", buyerId, sellerId, promoPosts.size());

        return new PromoPostsResponse(
                sellerId,
                seller.getName(),
                promoPosts.stream().map(postMapper::toDto).toList()
        );
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    private User findSellerById(long sellerId) {
        User user = findUserById(sellerId);
        if (!user.isSeller()) {
            throw new BusinessException("User " + user.getUserId() + " is not a seller");
        }
        return user;
    }

    private User findBuyerById(long buyerId) {
        User user = findUserById(buyerId);
        if (user.isSeller()) {
            throw new BusinessException("User " + user.getUserId() + " is a seller, not a buyer");
        }
        return user;
    }

    private void validatePromoDiscount(Double discount) {
        if (discount == null || discount <= 0 || discount >= 1) {
            throw new BusinessException("Invalid discount");
        }
    }

    private List<Post> sortPost(List<Post> posts, String order) {
        if (order == null || order.isBlank() || order.equalsIgnoreCase("date_desc")) {
            return posts;
        }
        if (order.equalsIgnoreCase("date_asc")) {
            return posts.stream()
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .toList();
        }
        throw new BusinessException("Invalid order param: " + order);
    }
}
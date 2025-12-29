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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class PostServiceImpl implements IPostService {

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
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + request.getUserId() + " not found"
                ));

        if (!user.isSeller()) {
            throw new BusinessException("User " + user.getId() + " is not a seller");
        }

        Post entity = postMapper.toEntity(request);
        entity.setPostId(0);
        entity.setUser(user);
        entity.setHasPromo(false);
        entity.setDiscount(0.0);

        postRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowedPostsResponse getFollowedPostsLastTwoWeeks(int userId, String order) {
        // valida se buyer existe
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        LocalDate since = LocalDate.now().minusWeeks(2);

        // pega sellers seguidos via query (sem LAZY)
        List<User> followedSellers = followRepository.findFollowedSellers(userId);
        if (followedSellers == null || followedSellers.isEmpty()) {
            return new FollowedPostsResponse(userId, List.of());
        }

        List<Post> posts = postRepository.findByUserInAndDateAfterOrderByDateDesc(
                new HashSet<>(followedSellers), since
        );

        posts = sortPost(posts, order);

        return new FollowedPostsResponse(userId, posts.stream().map(postMapper::toDto).toList());
    }

    @Override
    public void publishPromo(PromoPostPublishRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + request.getUserId() + " not found"
                ));

        if (!user.isSeller()) {
            throw new BusinessException("User " + user.getId() + " is not a seller");
        }

        validatePromoDiscount(request.getDiscount());

        Post entity = postMapper.toEntity(request);
        entity.setPostId(0);
        entity.setUser(user);
        entity.setHasPromo(true);
        entity.setDiscount(request.getDiscount());

        postRepository.save(entity);
    }

    @Override
    public PromoCountResponse getPromoCount(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!user.isSeller()) {
            throw new BusinessException("User " + user.getId() + " is not a seller");
        }

        long count = postRepository.countByUserIdAndHasPromoTrue(userId);
        return new PromoCountResponse(userId, user.getName(), Math.toIntExact(count));
    }

    public PromoPostsResponse getPromoPosts(int userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!seller.isSeller()) throw new BusinessException("User " + seller.getId() + " is not a seller");

        List<Post> promoPosts = postRepository.findByUserIdAndHasPromoTrueOrderByDateDesc(userId);

        return new PromoPostsResponse(userId, seller.getName(),
                promoPosts.stream().map(postMapper::toDto).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromoPostsResponse getPromoPostsForFollower(int buyerId, int sellerId) {

        userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + buyerId + " not found"));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + sellerId + " not found"));

        if (!seller.isSeller()) {
            throw new BusinessException("User " + seller.getId() + " is not a seller");
        }

        if (!followRepository.existsByFollowerIdAndSellerId(buyerId, sellerId)) {
            throw new BusinessException("Buyer does not follow this seller");
        }

        List<Post> promoPosts = postRepository.findByUserIdAndHasPromoTrueOrderByDateDesc(sellerId);

        return new PromoPostsResponse(
                sellerId,
                seller.getName(),
                promoPosts.stream().map(postMapper::toDto).toList()
        );
    }

    private void validatePromoDiscount(Double discount) {
        if (discount == null) throw new BusinessException("Discount is required for promo post");
        if (discount <= 0) throw new BusinessException("Discount must be greater than 0");
        if (discount > 100) throw new BusinessException("Discount cannot be greater than 100");
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
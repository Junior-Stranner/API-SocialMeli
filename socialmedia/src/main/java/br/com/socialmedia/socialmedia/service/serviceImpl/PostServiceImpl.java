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
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.IPostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements IPostService {

    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    public PostServiceImpl(UserRepository userRepository, PostMapper postMapper, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.postRepository = postRepository;
    }


    @Override
    public void publish(PostPublishRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.getUserId() + " not found"));

     if(!user.isSeller()){
         throw new BusinessException("User " + user.getId() + " is not a seller");
       }

        Post entity = postMapper.toEntity(request);
        entity.setPostId(0);
        entity.setUser(user);
        entity.setHasPromo(false);
        entity.setDiscount(0.0);
        postRepository.save(entity);

        postRepository.save(entity);
    }

    @Override
    public FollowedPostsResponse getFollowedPostsLastTwoWeeks(int userId, String order) {

       User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        LocalDate since = LocalDate.now().minusWeeks(2);

        Set<User> followed = user.getFollowed();
        if (followed == null || followed.isEmpty()) {
            return new FollowedPostsResponse(userId, List.of());
        }
        List<Post> promoPosts = postRepository.findByUserInAndDateAfterOrderByDateDesc(followed, since);
        promoPosts = sortPost(promoPosts, order);

        return new FollowedPostsResponse(userId, promoPosts.stream().map(postMapper::toDto).toList());
    }

    @Override
    public void publishPromo(PromoPostPublishRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.getUserId() + " not found"));

        if(!user.isSeller()) {
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

    @Override
    public PromoPostsResponse getPromoPosts(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!user.isSeller()) {
            throw new BusinessException("User " + user.getId() + " is not a seller");
        }

        List<Post> promoPosts = postRepository.findByUserIdAndHasPromoTrueOrderByDateDesc(userId);

        return new PromoPostsResponse(
                userId,
                user.getName(),
                promoPosts.stream().map(postMapper::toDto).toList()
        );
    }

    private void validatePromoDiscount(Double discount) {
        if (discount == null) throw new BusinessException("Discount is required for promo post");
        if (discount <= 0) throw new BusinessException("Discount must be greater than 0");
        if (discount > 100) throw new BusinessException("Discount cannot be greater than 100");
    }

    public List<Post> sortPost(List<Post> posts, String order){
        if (order == null || order.isBlank() || order.equalsIgnoreCase("date_desc")) {
            return posts;
        }
        if (order.equalsIgnoreCase("date_asc")) {
            return posts.stream()
                    .sorted((a,b) -> a.getDate().compareTo(b.getDate()))
                    .toList();
        }
        throw new BusinessException("Invalid order param: " + order);
    }
}

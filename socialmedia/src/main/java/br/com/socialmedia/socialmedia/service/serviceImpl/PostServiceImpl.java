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
import br.com.socialmedia.socialmedia.service.IPostServuce;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements IPostServuce {

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
        User user = userRepository.findById(request.user_id())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.user_id() + " not found"));

     if(!user.isSeller()){
         throw new BusinessException("User " + user.getId() + " is not a seller");
       }

       Post entity = postMapper.toEntity(request);
        entity.setUser(user);
        // US-0005: post normal
        entity.setHasPromo(false);
        entity.setDiscount(0.0);

        postRepository.save(entity);

    }

    @Override
    public FollowedPostsResponse getFollowedPostsLastTwoWeeks(int userId, String order) {
        return null;
    }

    @Override
    public void publishPromo(PromoPostPublishRequest request) {
        User user = userRepository.findById(request.user_id())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.user_id() + " not found"));

        if(!user.isSeller()) {
            throw new BusinessException("User " + user.getId() + " is not a seller");
        }

        validatePromoDiscount(request.discount());

        Post entity = postMapper.toEntity(request);
        entity.setUser(user);
        // US-0005: post normal
        entity.setHasPromo(true);
        entity.setDiscount(request.discount());

        postRepository.save(entity);
    }

    @Override
    public PromoCountResponse getPromoCount(int userId) {

        return null;
    }

    @Override
    public PromoPostsResponse getPromoPosts(int userId) {
        return null;
    }

    private void validatePromoDiscount(Double discount) {
        if (discount == null) throw new BusinessException("Discount is required for promo post");
        if (discount <= 0) throw new BusinessException("Discount must be greater than 0");
        if (discount > 100) throw new BusinessException("Discount cannot be greater than 100");
    }
}

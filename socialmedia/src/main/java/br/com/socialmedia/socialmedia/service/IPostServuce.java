package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoCountResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoPostsResponse;
import org.springframework.stereotype.Service;

@Service
public interface IPostServuce {

    // US-0005
    void publish(PostPublishRequest request);

    // US-0006 (+ US-0009 order by date)
    FollowedPostsResponse getFollowedPostsLastTwoWeeks(int userId, String order);

    // US-0010
    void publishPromo(PromoPostPublishRequest request);

    // US-0011
    PromoCountResponse getPromoCount(int userId);

    // US-0012 (bônus)
    PromoPostsResponse getPromoPosts(int userId);
}

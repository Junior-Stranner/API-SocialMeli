package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoCountResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoPostsResponse;

public interface IPostService {

    void publish(PostPublishRequest request);

    FollowedPostsResponse getFollowedPostsLastTwoWeeks(long userId, String order);

    void publishPromo(PromoPostPublishRequest request);

    PromoCountResponse getPromoCount(long userId);

    PromoPostsResponse getPromoPostsForFollower(long buyerId, long sellerId);

    PromoPostsResponse getPromoPosts(long userId);
}
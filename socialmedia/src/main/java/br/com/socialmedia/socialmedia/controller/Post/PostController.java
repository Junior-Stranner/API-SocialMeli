package br.com.socialmedia.socialmedia.controller.Post;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoCountResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoPostsResponse;
import br.com.socialmedia.socialmedia.service.IPostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController implements PostControllerDocs{

    private final IPostService postService;

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @PostMapping("/products/publish")
    public ResponseEntity<Void> publish(@Valid @RequestBody PostPublishRequest request) {
        postService.publish(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/products/followed/{userId}/list")
    public ResponseEntity<FollowedPostsResponse> getFollowedPostsLastTwoWeeks(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "date_desc") String order
    ) {
        FollowedPostsResponse response = postService.getFollowedPostsLastTwoWeeks(userId, order);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/promo-post")
    public ResponseEntity<Void> publishPromo(@Valid @RequestBody PromoPostPublishRequest request) {
        postService.publishPromo(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/products/promo-post/count")
    public ResponseEntity<PromoCountResponse> getPromoCount(@RequestParam int userId) {
        PromoCountResponse response = postService.getPromoCount(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/promo-post/list")
    public ResponseEntity<PromoPostsResponse> getPromoPosts(
            @RequestParam int buyerId,
            @RequestParam int sellerId
    ) {
        PromoPostsResponse response = postService.getPromoPostsForFollower(buyerId, sellerId);
        return ResponseEntity.ok(response);
    }
}
package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.SellerDto;
import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.PostResponse;
import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.Product;
import br.com.socialmedia.socialmedia.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    private final ModelMapper modelMapper;

    public PostMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Post toEntity(PostPublishRequest request) {
        Post post = modelMapper.map(request, Post.class);

        if (request.getProduct() != null) {
            Product p = modelMapper.map(request.getProduct(), Product.class);
            post.setProduct(p);
        }
        return post;
    }

    public Post toEntity(PromoPostPublishRequest request) {
        Post post = modelMapper.map(request, Post.class);

        if (request.getProduct() != null) {
            Product p = modelMapper.map(request.getProduct(), Product.class);
            post.setProduct(p);
        }
        return post;
    }

    public PostResponse toDto(Post post) {
        PostResponse response = modelMapper.map(post, PostResponse.class);
        response.setUserId(post.getUser().getUserId());
        response.setPostId(post.getPostId());

        response.setSeller(toSellerSummary(post.getUser()));
        return response;
    }

    public SellerDto toSellerSummary(User seller) {
        if (seller == null) {
            return null;
        }
        return new SellerDto(seller.getUserId(), seller.getName());
    }

    public List<PostResponse> toResponseList(List<Post> posts) {
        return posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
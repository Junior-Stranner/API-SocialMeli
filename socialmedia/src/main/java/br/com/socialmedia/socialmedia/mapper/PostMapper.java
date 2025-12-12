package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.SellerDto;
import br.com.socialmedia.socialmedia.dto.request.PostRequest;
import br.com.socialmedia.socialmedia.dto.response.PostResponse;
import br.com.socialmedia.socialmedia.entity.Post;
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


    public Post toEntity(PostRequest request) {
        Post post = modelMapper.map(request, Post.class);
        return post;
    }

    public PostResponse toDto(Post post) {
        PostResponse response = modelMapper.map(post, PostResponse.class);
        response.setSeller(toSellerSummary(post.getSeller()));
        return response;
    }



    public SellerDto toSellerSummary(User seller) {
        if (seller == null) {
            return null;
        }
        return new SellerDto(seller.getId(), seller.getName());
    }

    public List<PostResponse> toResponseList(List<Post> posts) {
        return posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
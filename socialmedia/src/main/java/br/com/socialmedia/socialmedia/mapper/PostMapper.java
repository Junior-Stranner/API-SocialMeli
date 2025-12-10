package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.SellerDto;
import br.com.socialmedia.socialmedia.dto.request.PostRequest;
import br.com.socialmedia.socialmedia.dto.response.PostResponse;
import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    Post toEntity(PostRequest dto);

    @Mapping(target = "seller", source = "seller", qualifiedByName = "toSellerSummary")
    PostResponse toDto(Post entity);

    default SellerDto toSellerSummary(User seller) {
        return seller == null ? null : new SellerDto(seller.getId(), seller.getName());
    }

    List<PostResponse> toResponseList(List<Post> posts);
}

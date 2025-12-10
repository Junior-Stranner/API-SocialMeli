package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.request.UserRequest;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest dto);

    UserResponse toDto(User entity);

    FollowDto toFollow(User entity);

    List<FollowDto> toFollowList(List<User> users);
}
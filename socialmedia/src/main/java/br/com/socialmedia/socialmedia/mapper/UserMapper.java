package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.request.UserRequest;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toEntity(UserRequest request) {
        return modelMapper.map(request, User.class);
    }

    public UserResponse toDto(User user) {
        return new UserResponse(user.getUserId(), user.getName(), user.isSeller());
    }

    public FollowDto toFollow(User user) {
        return new FollowDto(user.getUserId(), user.getName());
    }

    public List<FollowDto> toFollowList(List<User> users) {
        return users.stream().map(this::toFollow).toList();
    }

    public List<UserResponse> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}
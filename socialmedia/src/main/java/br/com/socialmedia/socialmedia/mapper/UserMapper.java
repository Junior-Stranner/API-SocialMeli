package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.dto.request.UserRequest;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.entity.User;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

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
        return modelMapper.map(user, UserResponse.class);
    }

    public FollowDto toFollow(User user) {
        return modelMapper.map(user, FollowDto.class);
    }

    public List<UserResponse> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        }
        public List<FollowDto> toFollowList(List<User> users) {
            return users.stream()
                    .map(this::toFollow)
                    .collect(Collectors.toList());
        }
    }

  /*  User toEntity(UserRequest dto);

    UserResponse toDto(User entity);

    FollowDto toFollow(User entity);

    List<FollowDto> toFollowList(List<User> users);*/
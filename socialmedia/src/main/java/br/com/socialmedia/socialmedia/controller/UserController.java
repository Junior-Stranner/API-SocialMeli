package br.com.socialmedia.socialmedia.controller;

import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<UserResponse> follow(
            @PathVariable int userId,
            @PathVariable int userIdToFollow
    ) {
        UserResponse sellerDto = userService.follow(userId, userIdToFollow);
        return ResponseEntity.ok(sellerDto);
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<UserResponse> unfollow(
            @PathVariable int userId,
            @PathVariable int userIdToUnfollow
    ) {
        UserResponse sellerDto = userService.unfollow(userId, userIdToUnfollow);
        return ResponseEntity.ok(sellerDto);
    }
}

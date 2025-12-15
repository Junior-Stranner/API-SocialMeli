package br.com.socialmedia.socialmedia.controller;

import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.dto.response.UserResponse;
import br.com.socialmedia.socialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // US 0001 - Follow
    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<UserResponse> follow(
            @PathVariable int userId,
            @PathVariable int userIdToFollow
    ) {
        return ResponseEntity.ok(userService.follow(userId, userIdToFollow));
    }

    // US 0007 - Unfollow
    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<UserResponse> unfollow(
            @PathVariable int userId,
            @PathVariable int userIdToUnfollow
    ) {
        return ResponseEntity.ok(userService.unfollow(userId, userIdToUnfollow));
    }

    // US 0002 - Followers count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountDto> getFollowersCount(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getFollowersCount(userId));
    }

    // US 0003 - Followers list (Quem me segue?) + order=name_asc|name_desc
    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersListDto> getFollowersList(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {

        return ResponseEntity.ok(userService.getFollowersList(userId));
    }

    // Extra (se você quiser expor a lista "crua" de followers como List<UserResponse>)
    // GET /api/v1/users/{userId}/followers?order=name_asc|name_desc
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowers(userId, order));
    }

    // US 0004 - Followed list (Quem estou seguindo?) + order=name_asc|name_desc
    // No documento: GET /users/{userId}/followed/list?order=...
    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<List<UserResponse>> getFollowed(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowed(userId, order));
    }
}
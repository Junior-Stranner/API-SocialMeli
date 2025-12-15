package br.com.socialmedia.socialmedia.controller;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
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
    public ResponseEntity<Void> follow(@PathVariable int userId, @PathVariable int userIdToFollow) {
        userService.follow(userId, userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollow(@PathVariable int userId, @PathVariable int userIdToUnfollow) {
        userService.unfollow(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    // US 0002 - Followers count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountDto> getFollowersCount(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getFollowersCount(userId));
    }

    // US 0003 - Followers list (Quem me segue?) + order
    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersListDto> getFollowersList(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowersList(userId, order));
    }

    // US 0004 - Followed list (Quem estou seguindo?) + order (conforme documento: wrapper)
    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<FollowedListDto> getFollowedList(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowedList(userId, order));
    }

  /*
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowers(userId, order));
    }

    @GetMapping("/{userId}/followed")
    public ResponseEntity<List<UserResponse>> getFollowed(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowed(userId, order));
    }*/
}
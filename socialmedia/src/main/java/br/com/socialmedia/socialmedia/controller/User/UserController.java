package br.com.socialmedia.socialmedia.controller.User;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import br.com.socialmedia.socialmedia.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs{

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // US 0001 - Follow
    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> follow(@PathVariable long userId, @PathVariable long userIdToFollow) {
        userService.follow(userId, userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollow(@PathVariable long userId, @PathVariable long userIdToUnfollow) {
        userService.unfollow(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    // US 0002 - Followers count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountDto> getFollowersCount(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getFollowersCount(userId));
    }

    // US 0003 - Followers list (Quem me segue?) + order
    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersListDto> getFollowersList(
            @PathVariable long userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowersList(userId, order));
    }

    // US 0004 - Followed list (Quem estou seguindo?) + order (conforme documento: wrapper)
    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<FollowedListDto> getFollowedList(
            @PathVariable long userId,
            @RequestParam(required = false, defaultValue = "name_asc") String order
    ) {
        return ResponseEntity.ok(userService.getFollowedList(userId, order));
    }

}
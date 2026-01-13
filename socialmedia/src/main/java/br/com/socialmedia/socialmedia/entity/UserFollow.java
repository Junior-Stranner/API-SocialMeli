package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_follow", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "seller_id"}))
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quem segue (buyer)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // Quem é seguido (seller)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @CreationTimestamp
    @Column(name = "followed_at", nullable = false, updatable = false)
    private LocalDateTime followedAt;

    protected UserFollow() {
    }

    public UserFollow(User follower, User seller) {
        this.follower = follower;
        this.seller = seller;
        this.followedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.followedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getFollower() { return follower; }
    public User getSeller() { return seller; }
    public LocalDateTime getFollowedAt() { return followedAt; }
}
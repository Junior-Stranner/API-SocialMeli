package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Table(name = "TB_USER")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USER_NAME", nullable = false, length = 80, unique = true)
    @Size(max = 80)
    @NotBlank
    private String name;

    @Column(name = "is_seller", nullable = false)
    private boolean seller;

    @ManyToMany
    @JoinTable(name = "user_followers",
            joinColumns = @JoinColumn(name = "seller_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> followed = new HashSet<>();


    public User(String name, boolean seller) {
        this.name = name;
        this.seller = seller;
    }
    public User() {

    }

    public void addFollower(User follower) {
        if (follower == null || follower.equals(this)) {
            return;
        }
        this.followers.add(follower);
        follower.followed.add(this);
    }

    public void removeFollower(User follower) {
        if (follower == null) {
            return;
        }
        this.followers.remove(follower);
        follower.followed.remove(this);
    }

    public boolean isFollowing(User user) {
        return this.followed.contains(user);
    }

    public boolean isFollowedBy(User user) {
        return this.followers.contains(user);
    }

    public int getFollowersCount() {
        return this.followers.size();
    }

    public int getFollowedCount() {
        return this.followed.size();
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSeller() {
        return seller;
    }

    public void setSeller(boolean seller) {
        this.seller = seller;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowed() {
        return followed;
    }

    public void setFollowed(Set<User> followed) {
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seller=" + seller +
                ", followersCount=" + followers.size() +
                ", followedCount=" + followed.size() +
                '}';
    }
}
package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Table(name = "tb_user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_name", nullable = false, length = 80, unique = true)
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
    private Set<User> following = new HashSet<>();


    public User(int id,String name, boolean seller) {
        this.id = id;
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
        follower.following.add(this);
    }

    public void removeFollower(User follower) {
        if (follower == null) {
            return;
        }
        this.followers.remove(follower);
        follower.following.remove(this);
    }

    public void follow(User user) {
        if (user == null || user.equals(this)) {
            return;
        }
        this.following.add(user);
        user.followers.add(this);
    }

    public void unfollow(User user) {
        if (user == null) {
            return;
        }
        this.following.remove(user);
        user.followers.remove(this);
    }

    public boolean isFollowing(User user) {
        return this.following.contains(user);
    }

    public boolean isFollowedBy(User user) {
        return this.followers.contains(user);
    }

    public int getFollowersCount() {
        return this.followers.size();
    }

    public int getFollowingCount() {
        return this.following.size();
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

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seller=" + seller +
                ", followersCount=" + followers.size() +
                ", followingCount=" + following.size() +
                '}';
    }
}
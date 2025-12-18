package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT COUNT(*) FROM user_followers uf WHERE uf.seller_id = :sellerId", nativeQuery = true)
    int countFollowers(@Param("sellerId") int sellerId);


    @Query("""
    SELECT f FROM User u
    JOIN u.followers f
    WHERE u.id = :sellerId
    ORDER BY f.name ASC
    """)
    List<User> findFollowersOrderByNameAsc(@Param("sellerId") int sellerId);


    @Query("""
    SELECT f FROM User u
    JOIN u.followers f
    WHERE u.id = :sellerId
    ORDER BY f.name DESC
    """)
    List<User> findFollowersOrderByNameDesc(@Param("sellerId") int sellerId);


    @Query("""
    SELECT f FROM User u
    JOIN u.followed f
    WHERE u.id = :userId
    ORDER BY f.name ASC
    """)
    List<User> findFollowedOrderByNameAsc(@Param("userId") int userId);


    @Query("""
    SELECT f FROM User u
    JOIN u.followed f
    WHERE u.id = :userId
    ORDER BY f.name DESC
    """)
    List<User> findFollowedOrderByNameDesc(@Param("userId") int userId);
}

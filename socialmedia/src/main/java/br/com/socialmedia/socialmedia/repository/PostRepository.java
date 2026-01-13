package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.user.userId = :userId AND p.hasPromo = true ORDER BY p.date DESC")
    List<Post> findByUserIdAndHasPromoTrueOrderByDateDesc(@Param("userId") long userId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.userId = :userId AND p.hasPromo = true")
    long countByUserIdAndHasPromoTrue(@Param("userId") long userId);

    @Query("SELECT p FROM Post p WHERE p.user IN :users AND p.date >= :date ORDER BY p.date DESC")
    List<Post> findByUserInAndDateAfterOrderByDateDesc(@Param("users") Set<User> users,
                                                       @Param("date") LocalDate date);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.user u
        WHERE u.userId = :sellerId AND p.hasPromo = true
        ORDER BY p.date DESC
    """)
    List<Post> findPromoPostsBySellerIdFetchUser(@Param("sellerId") long sellerId);
}
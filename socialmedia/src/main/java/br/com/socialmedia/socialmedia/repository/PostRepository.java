package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByUserIdAndHasPromoTrueOrderByDateDesc(int userId);

    long countByUserIdAndHasPromoTrue(int userId);

    List<Post> findByUserInAndDateAfterOrderByDateDesc(Set<User> users, LocalDate date);
}

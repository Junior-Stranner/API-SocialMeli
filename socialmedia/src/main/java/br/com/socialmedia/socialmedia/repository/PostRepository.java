package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}

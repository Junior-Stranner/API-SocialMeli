package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}

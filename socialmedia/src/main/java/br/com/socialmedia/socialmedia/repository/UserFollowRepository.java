package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {

    //Verifica se follow existe (COM @Query)
    @Query("SELECT COUNT(uf) > 0 FROM UserFollow uf WHERE uf.follower.userId = :followerId AND uf.seller.userId = :sellerId")
    boolean existsByFollowerIdAndSellerId(@Param("followerId") int followerId, @Param("sellerId") int sellerId);

    //Conta seguidores (COM @Query)
    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.seller.userId = :sellerId")
    long countBySellerId(@Param("sellerId") int sellerId);

    //Deleta follow (COM @Query)
    @Modifying
    @Query("DELETE FROM UserFollow uf WHERE uf.follower.userId = :followerId AND uf.seller.userId = :sellerId")
    void deleteByFollowerIdAndSellerId(@Param("followerId") int followerId, @Param("sellerId") int sellerId);

    // Lista vendedores seguidos
    @Query("SELECT uf.seller FROM UserFollow uf WHERE uf.follower.userId = :userId")
    List<User> findFollowedSellers(@Param("userId") int userId);

    // Lista seguidores ordenados
    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.seller.userId = :sellerId ORDER BY uf.follower.name ASC")
    List<User> findFollowersOrderByNameAsc(@Param("sellerId") int sellerId);

    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.seller.userId = :sellerId ORDER BY uf.follower.name DESC")
    List<User> findFollowersOrderByNameDesc(@Param("sellerId") int sellerId);

    // Lista seguidos ordenados
    @Query("SELECT uf.seller FROM UserFollow uf WHERE uf.follower.userId = :userId ORDER BY uf.seller.name ASC")
    List<User> findFollowedOrderByNameAsc(@Param("userId") int userId);

    @Query("SELECT uf.seller FROM UserFollow uf WHERE uf.follower.userId = :userId ORDER BY uf.seller.name DESC")
    List<User> findFollowedOrderByNameDesc(@Param("userId") int userId);
}
package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {


    boolean existsByFollowerIdAndSellerId(int followerId, int sellerId);

    void deleteByFollowerIdAndSellerId(int followerId, int sellerId);

    long countBySellerId(int sellerId);

    @Query("""
        select uf.follower
        from UserFollow uf
        where uf.seller.id = :sellerId
        order by uf.follower.name asc
    """)
    List<User> findFollowersOrderByNameAsc(int sellerId);

    @Query("""
        select uf.follower
        from UserFollow uf
        where uf.seller.id = :sellerId
        order by uf.follower.name desc
    """)
    List<User> findFollowersOrderByNameDesc(int sellerId);

    @Query("""
        select uf.seller
        from UserFollow uf
        where uf.follower.id = :buyerId
        order by uf.seller.name asc
    """)
    List<User> findFollowedOrderByNameAsc(int buyerId);

    @Query("""
        select uf.seller
        from UserFollow uf
        where uf.follower.id = :buyerId
        order by uf.seller.name desc
    """)
    List<User> findFollowedOrderByNameDesc(int buyerId);

    @Query("""
    select uf.seller
    from UserFollow uf
    where uf.follower.id = :buyerId
""")
    List<User> findFollowedSellers(int buyerId);



}

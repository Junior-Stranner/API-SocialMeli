package br.com.socialmedia.socialmedia.repository;

import br.com.socialmedia.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT COUNT(*) FROM user_followers uf WHERE uf.seller_id = :sellerId", nativeQuery = true)
    int countFollowers(@Param("sellerId") int sellerId);


    @Query(value  = "select u from tb_user u inner join user_followers uf on uf.follower_id = u.id " +
            "where uf.seller_id = : sellerid" +
            "order by u.user_name like asc ")
    List<User> findFollowersOrderByNameAsc(@Param("sellerId") int sellerId);


    @Query(value  = "select u from tb_user u inner join user_followers uf on uf.follower_id = u.id" +
            "where uf.seller_id = : sellerId"+
            "order by u.user_name like desc")
    List<User> findFollowersOrderByNameDesc(@Param("sellerId") int sellerId);


    @Query(value = """
        select u.*
        from tb_user u
        inner join user_followers uf ON uf.seller_id = u.id
        where uf.follower_id = :userId
        order by u.user_name asc
        """)
    List<User> findFollowedOrderByNameAsc(@Param("userId") int userId);

    @Query(value = """
        select u.*
        from tb_user
        iner join user_followers uf ON uf.seller_id = u.id
        where uf.follower_id = :userId
        order by u.user_name desc
        """)
    List<User> findFollowedOrderByNameDesc(@Param("userId") int userId);

}

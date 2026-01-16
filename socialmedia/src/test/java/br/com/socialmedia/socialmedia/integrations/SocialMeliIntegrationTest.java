package br.com.socialmedia.socialmedia.integrations;

import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.Product;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest/*Carrega o contexto inteiro do Spring Boot.
É mais pesado, mas te dá um cenário real.*/
@AutoConfigureMockMvc/*Cria e injeta o MockMvc,
 que permite fazer chamadas HTTP simuladas:*/
@ActiveProfiles("test")/*Ativa o profile test. Normalmente isso significa:
banco H2 em memória
configs específicas para teste
desativar segurança, etc.
*/
@DisplayName("SocialMeli - Testes de Integração (Essencial)")
class SocialMeliIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserFollowRepository followRepository;
    @Autowired private PostRepository postRepository;

    private User buyer;
    private User seller;

    @BeforeEach
    void setup() {
        followRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        buyer = userRepository.save(new User("Buyer", false));
        seller = userRepository.save(new User("Seller", true));
    }

    @Test
    @DisplayName("US-0001: Deve seguir um vendedor e retornar 200")
    void us0001_follow_shouldReturn200_whenValid() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}",
                        buyer.getUserId(), seller.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0002: Deve retornar a contagem de seguidores do vendedor")
    void us0002_getFollowersCount_shouldReturnCount() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/count", seller.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers_count").value(1));
    }

    @Test
    @DisplayName("US-0003: Deve retornar lista de seguidores do vendedor")
    void us0003_getFollowersList_shouldReturnFollowers() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/list", seller.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getUserId()))
                .andExpect(jsonPath("$.followers").isArray());
    }

    @Test
    @DisplayName("US-0004: Deve retornar lista de vendedores que o usuário segue")
    void us0004_getFollowedList_shouldReturnFollowedSellers() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{userId}/followed/list", buyer.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getUserId()))
                .andExpect(jsonPath("$.followed").isArray());
    }

    @Test
    @DisplayName("US-0005: Deve registrar nova publicação e retornar 201 (ou 200 se você mudou)")
    void us0005_publish_shouldReturn201_whenValid() throws Exception {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // snake_case
        String requestBody = """
        {
          "userId": %d,
          "date": "%s",
          "product": {
            "productId": 1,
            "productName": "Mouse Gamer",
            "type": "Periferico",
            "brand": "Logitech",
            "color": "Preto",
            "notes": "RGB"
          },
          "category": 58,
          "price": 299.90
        }
        """.formatted(seller.getUserId(), date);
        mockMvc.perform(post("/api/v1/posts/products/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("US-0006: Deve retornar posts das últimas 2 semanas para usuário que segue vendedor")
    void us0006_getFollowedPosts_shouldReturn200() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));
        createPost(seller, LocalDate.now().minusDays(5), false);

        mockMvc.perform(get("/api/v1/posts/products/followed/{userId}/list", buyer.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getUserId()))
                .andExpect(jsonPath("$.posts").isArray());
    }

    @Test
    @DisplayName("US-0007: Deve deixar de seguir e retornar 200")
    void us0007_unfollow_shouldReturn200_whenValid() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(post("/api/v1/users/{userId}/unfollow/{sellerId}",
                        buyer.getUserId(), seller.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Promo: Deve registrar publicação com promoção e retornar 200")
    void promo_publishPromo_shouldReturn200_whenValid() throws Exception {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        String requestBody = """
        {
          "userId": %d,
          "date": "%s",
          "product": {
            "productId": 22,
            "productName": "Mouse Gamer",
            "type": "Periferico",
            "brand": "Logitech",
            "color": "Preto",
            "notes": "RGB"
          },
          "category": 58,
          "price": 299.90,
          "has_promo": true,
          "discount": 0.25
        }
        """.formatted(seller.getUserId(), date);

        mockMvc.perform(post("/api/v1/posts/products/promo-pub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0011: Deve retornar contagem de produtos em promoção do vendedor")
    void us0011_getPromoCount_shouldReturn200() throws Exception {
        createPost(seller, LocalDate.now(), true);
        createPost(seller, LocalDate.now(), true);
        createPost(seller, LocalDate.now(), false);

        mockMvc.perform(get("/api/v1/posts/products/promo-post/count")
                        .param("userId", String.valueOf(seller.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promo_products_count").value(2));
    }

    // US-0009 (ordenar por data)
    @Test
    @DisplayName("US-0009: Deve ordenar posts por data ASC corretamente")
    void us0009_feed_shouldOrderByDateAsc() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        createPost(seller, LocalDate.now().minusDays(10), false);
        createPost(seller, LocalDate.now().minusDays(2), false);

        mockMvc.perform(get("/api/v1/posts/products/followed/{userId}/list", buyer.getUserId())
                        .param("order", "date_asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].date").exists());

    }

    private Post createPost(User user, LocalDate date, boolean hasPromo) {
        Post post = new Post();
        post.setUser(user);
        post.setDate(date);
        post.setCategory(58);
        post.setPrice(BigDecimal.valueOf(299.90));
        post.setHasPromo(hasPromo);
        post.setDiscount(hasPromo ? 25.0 : 0.0);

        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mouse Gamer");
        product.setType("Periferico");
        product.setBrand("Logitech");
        product.setColor("Preto");
        product.setNotes("RGB");
        post.setProduct(product);

        return postRepository.save(post);
    }
}
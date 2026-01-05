package br.com.socialmedia.socialmedia.integrations;

import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.Product;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SocialMeli - Testes de Integração")
public class SocialMeliIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowRepository followRepository;

    @Autowired
    private PostRepository postRepository;

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

    // ==================================================================================
    // US-0001: Seguir um vendedor
    // ==================================================================================

    @Test
    @DisplayName("US-0001: Deve retornar 200 ao seguir um vendedor")
    void follow_shouldReturn200_whenValid() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getUserId(), seller.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0001: Deve retornar 404 quando vendedor não existe")
    void follow_shouldReturn404_whenSellerNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getUserId(), 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("US-0001: Deve retornar 400 quando tenta seguir a si mesmo")
    void follow_shouldReturn400_whenFollowingSelf() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getUserId(), buyer.getUserId()))
                .andExpect(status().isBadRequest());
    }

    // ==================================================================================
    // US-0002: Obter contagem de seguidores
    // ==================================================================================

    @Test
    @DisplayName("US-0002: Deve retornar contagem de seguidores")
    void getFollowersCount_shouldReturn200() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/count", seller.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers_count").value(1));
    }

    // ==================================================================================
    // US-0003: Lista de seguidores
    // ==================================================================================

    @Test
    @DisplayName("US-0003: Deve retornar lista de seguidores")
    void getFollowersList_shouldReturn200() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/list", seller.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getUserId()))
                .andExpect(jsonPath("$.followers").isArray())
                .andExpect(jsonPath("$.followers[0].user_name").value("Buyer"));
    }

    // ==================================================================================
    // US-0004: Lista de seguidos
    // ==================================================================================

    @Test
    @DisplayName("US-0004: Deve retornar lista de vendedores seguidos")
    void getFollowedList_shouldReturn200() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(get("/api/v1/users/{userId}/followed/list", buyer.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getUserId()))
                .andExpect(jsonPath("$.followed").isArray())
                .andExpect(jsonPath("$.followed[0].user_name").value("Seller"));
    }

    // ==================================================================================
    // US-0005: Registrar publicação
    // ==================================================================================

    @Test
    @DisplayName("US-0005: Deve registrar nova publicação")
    void publish_shouldReturn200_whenValid() throws Exception {
        String requestBody = """
            {
                "user_id": %d,
                "date": "%s",
                "product": {
                    "product_id": 1,
                    "product_name": "Mouse Gamer",
                    "type": "Periferico",
                    "brand": "Logitech",
                    "color": "Preto",
                    "notes": "RGB"
                },
                "category": 58,
                "price": 299.90
            }
            """.formatted(seller.getUserId(), LocalDate.now());

        mockMvc.perform(post("/api/v1/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0005: Deve retornar 400 quando usuário não é vendedor")
    void publish_shouldReturn400_whenNotSeller() throws Exception {
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
            """.formatted(buyer.getUserId(), LocalDate.now());

        mockMvc.perform(post("/api/v1/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================================================================================
    // US-0006: Publicações das últimas 2 semanas
    // ==================================================================================

    @Test
    @DisplayName("US-0006: Deve retornar publicações das últimas 2 semanas")
    void getFollowedPosts_shouldReturn200() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));
        createPost(seller, LocalDate.now().minusDays(5), false);

        mockMvc.perform(get("/api/v1/posts/products/followed/{followedId}/list?order=date_asc", buyer.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(buyer.getUserId()))
                .andExpect(jsonPath("$.posts").isArray());
    }

    // ==================================================================================
    // US-0007: Deixar de seguir
    // ==================================================================================

    @Test
    @DisplayName("US-0007: Deve retornar 200 ao deixar de seguir")
    void unfollow_shouldReturn200_whenValid() throws Exception {
        followRepository.save(new UserFollow(buyer, seller));

        mockMvc.perform(post("/api/v1/users/{userId}/unfollow/{sellerId}", buyer.getUserId(), seller.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0007: Deve retornar 409 quando não está seguindo")
    void unfollow_shouldReturn409_whenNotFollowing() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/unfollow/{sellerId}", buyer.getUserId(), seller.getUserId()))
                .andExpect(status().isConflict());
    }

    // ==================================================================================
    // US-0008: Ordenação alfabética
    // ==================================================================================

    @Test
    @DisplayName("US-0008: Deve ordenar seguidores por nome ASC")
    void getFollowersList_shouldOrderByNameAsc() throws Exception {
        User buyer2 = userRepository.save(new User("Ana", false));
        followRepository.save(new UserFollow(buyer, seller));
        followRepository.save(new UserFollow(buyer2, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/list", seller.getUserId())
                        .param("order", "name_asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value("Ana"))
                .andExpect(jsonPath("$.followers[1].user_name").value("Buyer"));
    }

    @Test
    @DisplayName("US-0008: Deve ordenar seguidores por nome DESC")
    void getFollowersList_shouldOrderByNameDesc() throws Exception {
        User buyer2 = userRepository.save(new User("Ana", false));
        followRepository.save(new UserFollow(buyer, seller));
        followRepository.save(new UserFollow(buyer2, seller));

        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/list", seller.getUserId())
                        .param("order", "name_desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value("Buyer"))
                .andExpect(jsonPath("$.followers[1].user_name").value("Ana"));
    }

    // ==================================================================================
    // US-0010: Publicação com promoção
    // ==================================================================================

    @Test
    @DisplayName("US-0010: Deve registrar publicação com promoção")
    void publishPromo_shouldReturn200_whenValid() throws Exception {
        String requestBody = """
                {
                   "userId": 3,
                   "date": "18-12-2025",
                   "product": {
                     "productId": 22,
                     "productName": "Mouse Gamer",
                     "type": "Periferico",
                     "brand": "Logitech",
                     "color": "Preto,
                     "notes": "RGB"
                   },
                   "category": 58,
                   "price": 299.90,
                   "has_promo": true,
                   "discount": 0.25
                 }
            """.formatted(seller.getUserId(), LocalDate.now());

        mockMvc.perform(post("/api/v1/posts/products/promo-pub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    // ==================================================================================
    // US-0011: Contagem de promoções
    // ==================================================================================

    @Test
    @DisplayName("US-0011: Deve retornar contagem de produtos em promoção")
    void getPromoCount_shouldReturn200() throws Exception {
        createPost(seller, LocalDate.now(), true);
        createPost(seller, LocalDate.now(), true);
        createPost(seller, LocalDate.now(), false);

        mockMvc.perform(get("/api/v1/post/products/promo-post/count")
                        .param("user_id", String.valueOf(seller.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promo_products_count").value(2));
    }

    // ==================================================================================
    // Método auxiliar
    // ==================================================================================

    private Post createPost(User user, LocalDate date, boolean hasPromo) {
        Post post = new Post();
        post.setUser(user);
        post.setDate(date);
        post.setCategory(58);
        post.setPrice(299.90);
        post.setHasPromo(hasPromo);
        post.setDiscount(hasPromo ? 15.0 : 0.0);

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
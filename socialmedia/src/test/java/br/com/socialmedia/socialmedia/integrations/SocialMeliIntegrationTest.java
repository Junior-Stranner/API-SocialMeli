package br.com.socialmedia.socialmedia.integrations;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

        buyer = userRepository.save(new User("Buyer 1", false));
        seller = userRepository.save(new User("Seller 1", true));
    }

    // ==================================================================================
    // US-0001: Seguir um vendedor
    // ==================================================================================

    @Test
    @DisplayName("US-0001: Deve retornar 200 ao seguir um vendedor")
    void follow_shouldReturn200_whenValid() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getId(), seller.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US-0001: Deve retornar 404 quando vendedor não existe")
    void follow_shouldReturn404_whenSellerNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getId(), 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("US-0001: Deve retornar 400 quando tenta seguir a si mesmo")
    void follow_shouldReturn400_whenFollowingSelf() throws Exception {
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getId(), buyer.getId()))
                .andExpect(status().isBadRequest());
    }

    // ==================================================================================
    // US-0007: Deixar de seguir um vendedor
    // ==================================================================================

    @Test
    @DisplayName("US-0007: Deve retornar 200 ao deixar de seguir")
    void unfollow_shouldReturn200_whenValid() throws Exception {
        // Primeiro segue
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getId(), seller.getId()));

        // Depois deixa de seguir
        mockMvc.perform(post("/api/v1/users/{userId}/unfollow/{sellerId}", buyer.getId(), seller.getId()))
                .andExpect(status().isOk());
    }

    // ==================================================================================
    // US-0002: Obter contagem de seguidores
    // ==================================================================================

    @Test
    @DisplayName("US-0002: Deve retornar contagem de seguidores")
    void getFollowersCount_shouldReturn200() throws Exception {
        // Primeiro segue
        mockMvc.perform(post("/api/v1/users/{userId}/follow/{sellerId}", buyer.getId(), seller.getId()));

        // Verifica contagem
        mockMvc.perform(get("/api/v1/users/{sellerId}/followers/count", seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers_count").value(1));
    }
}
package br.com.socialmedia.socialmedia.integrations;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SocialMeliIntegrationTest {

    private final MockMvc mockMvc;

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public SocialMeliIntegrationTest(MockMvc mockMvc, UserRepository userRepository, PostRepository postRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }


    @BeforeEach
    void setup(){
        postRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("Buyer 1", false));
        userRepository.save(new User("Seller 1", true));
    }

    @Test
    void US0001_follow_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/follow/3"))
                .andExpect(status().isOk());
    }
}

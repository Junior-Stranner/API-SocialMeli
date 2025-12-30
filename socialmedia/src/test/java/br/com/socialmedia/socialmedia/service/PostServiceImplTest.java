/*package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.ProductRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.mapper.PostMapper;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("PostService - Testes Unitários")
class PostServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    // ==================================================================================
    // US-0005: Registrar uma nova publicação
    // ==================================================================================
    @Nested
    @DisplayName("US-0005: Registrar uma nova publicação")
    class PublishTests {

        @Test
        @DisplayName("Deve salvar publicação quando usuário é vendedor")
        void publish_shouldSavePost_whenUserIsSeller() {
            // Arrange
            User seller = new User("José", true);
            seller.setId(2);

            PostPublishRequest request = createPostRequest(2);
            Post entity = new Post();

            when(userRepository.findById(2)).thenReturn(Optional.of(seller));
            when(postMapper.toEntity(request)).thenReturn(entity);

            // Act
            postService.publish(request);

            // Assert
            assertAll(
                    () -> assertFalse(entity.isHasPromo(), "Post não deve ter promoção"),
                    () -> assertEquals(0.0, entity.getDiscount(), "Desconto deve ser 0"),
                    () -> assertEquals(seller, entity.getUser(), "Usuário deve ser o vendedor")
            );
            verify(postRepository).save(entity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando usuário não é vendedor")
        void publish_shouldThrowBusinessException_whenUserIsNotSeller() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            PostPublishRequest request = createPostRequest(1);

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

            // Act & Assert
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.publish(request)
            );

            verify(postRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando usuário não existe")
        void publish_shouldThrowNotFoundException_whenUserNotFound() {
            // Arrange
            PostPublishRequest request = createPostRequest(999);

            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    ChangeSetPersister.NotFoundException.class,
                    () -> postService.publish(request)
            );

            verify(postRepository, never()).save(any());
        }
    }

    // ==================================================================================
    // US-0010: Publicar produto em promoção
    // ==================================================================================
    @Nested
    @DisplayName("US-0010: Publicar produto em promoção")
    class PublishPromoTests {

        @Test
        @DisplayName("Deve salvar publicação promocional quando dados são válidos")
        void publishPromo_shouldSavePost_whenDataIsValid() {
            // Arrange
            User seller = new User("José", true);
            seller.setId(2);

            PromoPostPublishRequest request = createPromoPostRequest(2, 25.0);
            Post entity = new Post();

            when(userRepository.findById(2)).thenReturn(Optional.of(seller));
            when(postMapper.promoToEntity(request)).thenReturn(entity);

            // Act
            postService.publishPromo(request);

            // Assert
            verify(postRepository).save(entity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando desconto é inválido (maior que 100)")
        void publishPromo_shouldThrowBusinessException_whenDiscountInvalid() {
            // Arrange
            User seller = new User("José", true);
            seller.setId(2);

            PromoPostPublishRequest request = createPromoPostRequest(2, 200.0);

            when(userRepository.findById(2)).thenReturn(Optional.of(seller));

            // Act & Assert
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.publishPromo(request)
            );

            verify(postRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando desconto é negativo")
        void publishPromo_shouldThrowBusinessException_whenDiscountIsNegative() {
            // Arrange
            User seller = new User("José", true);
            seller.setId(2);

            PromoPostPublishRequest request = createPromoPostRequest(2, -10.0);

            when(userRepository.findById(2)).thenReturn(Optional.of(seller));

            // Act & Assert
            assertThrows(
                    BusinessException.class,
                    () -> postService.publishPromo(request)
            );

            verify(postRepository, never()).save(any());
        }
    }

    // ==================================================================================
    // US-0011: Obter quantidade de produtos em promoção
    // ==================================================================================
    @Nested
    @DisplayName("US-0011: Obter quantidade de produtos em promoção")
    class PromoCountTests {

        @Test
        @DisplayName("Deve retornar contagem de promoções quando usuário é vendedor - T-0007")
        void getPromoCount_shouldReturnCount_whenUserIsSeller() {
            // Arrange
            User seller = new User("José", true);
            seller.setId(2);

            when(userRepository.findById(2)).thenReturn(Optional.of(seller));
            when(postRepository.countByUserIdAndHasPromoTrue(2)).thenReturn(5L);

            // Act
            var response = postService.getPromoCount(2);

            // Assert
            assertAll(
                    () -> assertEquals(2, response.userId()),
                    () -> assertEquals("José", response.userName()),
                    () -> assertEquals(5L, response.promoProductsCount())
            );
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando usuário não é vendedor")
        void getPromoCount_shouldThrowBusinessException_whenUserIsNotSeller() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

            // Act & Assert
            assertThrows(
                    BusinessException.class,
                    () -> postService.getPromoCount(1)
            );
        }
    }

    // ==================================================================================
    // US-0006: Obter publicações das últimas duas semanas dos vendedores seguidos
    // T-0008: Verificar se as publicações são das últimas duas semanas
    // ==================================================================================
    @Nested
    @DisplayName("US-0006: Publicações das últimas duas semanas")
    class FollowedPostsTests {

        @Test
        @DisplayName("T-0008: Deve retornar apenas publicações das últimas duas semanas")
        void getFollowedPostsLastTwoWeeks_shouldReturnOnlyRecentPosts() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            User seller = new User("José", true);
            seller.setId(2);

            buyer.setFollowed(Set.of(seller));

            LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);

            Post recentPost = new Post();
            recentPost.setDate(LocalDate.now().minusDays(5));
            recentPost.setUser(seller);

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
            when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), eq(twoWeeksAgo)))
                    .thenReturn(List.of(recentPost));

            // Act
            var response = postService.getFollowedPostsLastTwoWeeks(1, "date_desc");

            // Assert
            assertNotNull(response);
            assertFalse(response.posts().isEmpty());
            assertTrue(response.posts().stream()
                    .allMatch(p -> p.date().isAfter(twoWeeksAgo) || p.date().isEqual(twoWeeksAgo)));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando usuário não segue ninguém")
        void getFollowedPostsLastTwoWeeks_shouldReturnEmpty_whenUserFollowsNobody() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);
            buyer.setFollowed(Set.of());

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

            // Act
            var response = postService.getFollowedPostsLastTwoWeeks(1, "date_desc");

            // Assert
            assertNotNull(response);
            assertTrue(response.posts().isEmpty());
            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("T-0005: Deve lançar BusinessException quando ordenação é inválida")
        void getFollowedPostsLastTwoWeeks_shouldThrowBusinessException_whenOrderInvalid() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            User seller = new User("José", true);
            seller.setId(2);

            buyer.setFollowed(Set.of(seller));

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
            when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), any()))
                    .thenReturn(List.of());

            // Act & Assert
            assertThrows(
                    BusinessException.class,
                    () -> postService.getFollowedPostsLastTwoWeeks(1, "invalid_order")
            );
        }

        @Test
        @DisplayName("T-0006: Deve ordenar por data descendente corretamente")
        void getFollowedPostsLastTwoWeeks_shouldOrderByDateDesc() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            User seller = new User("José", true);
            seller.setId(2);

            buyer.setFollowed(Set.of(seller));

            Post olderPost = new Post();
            olderPost.setDate(LocalDate.now().minusDays(10));
            olderPost.setUser(seller);

            Post newerPost = new Post();
            newerPost.setDate(LocalDate.now().minusDays(2));
            newerPost.setUser(seller);

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
            when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), any()))
                    .thenReturn(List.of(newerPost, olderPost));

            // Act
            var response = postService.getFollowedPostsLastTwoWeeks(1, "date_desc");

            // Assert
            assertNotNull(response);
            assertEquals(2, response.posts().size());
            assertTrue(response.posts().get(0).date().isAfter(response.posts().get(1).date()));
        }

        @Test
        @DisplayName("T-0006: Deve ordenar por data ascendente corretamente")
        void getFollowedPostsLastTwoWeeks_shouldOrderByDateAsc() {
            // Arrange
            User buyer = new User("Junior", false);
            buyer.setId(1);

            User seller = new User("José", true);
            seller.setId(2);

            buyer.setFollowed(Set.of(seller));

            Post olderPost = new Post();
            olderPost.setDate(LocalDate.now().minusDays(10));
            olderPost.setUser(seller);

            Post newerPost = new Post();
            newerPost.setDate(LocalDate.now().minusDays(2));
            newerPost.setUser(seller);

            when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
            when(postRepository.findByUserInAndDateAfterOrderByDateAsc(anySet(), any()))
                    .thenReturn(List.of(olderPost, newerPost));

            // Act
            var response = postService.getFollowedPostsLastTwoWeeks(1, "date_asc");

            // Assert
            assertNotNull(response);
            assertEquals(2, response.posts().size());
            assertTrue(response.posts().get(0).date().isBefore(response.posts().get(1).date()));
        }
    }

    // ==================================================================================
    // Métodos auxiliares para criar objetos de teste
    // ==================================================================================

    private PostPublishRequest createPostRequest(int userId) {
        PostPublishRequest request = new PostPublishRequest();
        request.setUserId(userId);
        request.setDate(LocalDate.now());
        request.setCategory(100);
        request.setPrice(10.0);

        ProductRequest product = new ProductRequest();
        product.setProductId(1);
        product.setProductName("Headset");
        product.setType("Gamer");
        product.setBrand("Razer");
        product.setColor("Green");
        product.setNotes("OK");
        request.setProduct(product);

        return request;
    }

    private PromoPostPublishRequest createPromoPostRequest(int userId, double discount) {
        PromoPostPublishRequest request = new PromoPostPublishRequest();
        request.setUserId(userId);
        request.setDate(LocalDate.now());
        request.setCategory(100);
        request.setPrice(10.0);
        request.setHasPromo(true);
        request.setDiscount(discount);
        request.setProduct(new ProductRequest());

        return request;
    }
}
 */
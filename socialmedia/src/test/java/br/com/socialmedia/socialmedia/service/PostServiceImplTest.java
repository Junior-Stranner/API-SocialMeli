package br.com.socialmedia.socialmedia.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PostServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;
    @Mock private PostRepository postRepository;

    @InjectMocks private PostServiceImpl postService;

    @Test
    void publish_shouldSavePost_whenUserIsSeller() {
        // Arrange
        User seller = new User("José", true);
        seller.setId(2);

        PostPublishRequest req = new PostPublishRequest();
        req.setUserId(2);
        req.setDate(LocalDate.now());
        req.setCategory(100);
        req.setPrice(10.0);

        ProductRequest product = new ProductRequest();
        product.setProductId(1);
        product.setProductName("Headset");
        product.setType("Gamer");
        product.setBrand("Razer");
        product.setColor("Green");
        product.setNotes("OK");
        req.setProduct(product);

        Post entity = new Post();

        when(userRepository.findById(2)).thenReturn(Optional.of(seller));
        when(postMapper.toEntity(req)).thenReturn(entity);

        // Act
        postService.publish(req);

        // Assert
        assertFalse(entity.isHasPromo());
        assertEquals(0.0, entity.getDiscount());
        assertEquals(seller, entity.getUser());
        verify(postRepository).save(entity);
    }

    @Test
    void publish_shouldThrowBusinessException_whenUserIsNotSeller() {
        // Arrange
        User buyer = new User("Junior", false);
        buyer.setId(1);

        PostPublishRequest req = new PostPublishRequest();
        req.setUserId(1);
        req.setDate(LocalDate.now());
        req.setCategory(100);
        req.setPrice(10.0);
        req.setProduct(new ProductRequest());

        when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.publish(req));
        verify(postRepository, never()).save(any());
    }

    @Test
    void publishPromo_shouldThrowBusinessException_whenDiscountInvalid() {
        User seller = new User("José", true);
        seller.setId(2);

        PromoPostPublishRequest req = new PromoPostPublishRequest();
        req.setUserId(2);
        req.setDate(LocalDate.now());
        req.setCategory(100);
        req.setPrice(10.0);
        req.setHasPromo(true);
        req.setDiscount(200.0);
        req.setProduct(new ProductRequest());

        when(userRepository.findById(2)).thenReturn(Optional.of(seller));

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.publishPromo(req));
        verify(postRepository, never()).save(any());
    }

    @Test
    void getPromoCount_shouldThrowBusinessException_whenUserIsNotSeller() {
        // Arrange
        User buyer = new User("Junior", false);
        buyer.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.getPromoCount(1));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_shouldReturnEmpty_whenUserFollowsNobody() {
        // Arrange
        User buyer = new User("Junior", false);
        buyer.setId(1);
        buyer.setFollowed(Set.of()); // se existir setter

        when(userRepository.findById(1)).thenReturn(Optional.of(buyer));

        // Act
        var response = postService.getFollowedPostsLastTwoWeeks(1, "date_desc");

        // Assert
        assertNotNull(response);
        assertTrue(response.posts().isEmpty() || response.posts().isEmpty());
        verifyNoInteractions(postRepository);
    }

    @Test
    void getFollowedPostsLastTwoWeeks_shouldThrowBusinessException_whenOrderInvalid() {
        // Arrange
        User buyer = new User("Junior", false);
        buyer.setId(1);

        User seller = new User("José", true);
        seller.setId(2);

        buyer.setFollowed(Set.of(seller)); // se existir setter

        when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
        when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), any()))
                .thenReturn(List.of());

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.getFollowedPostsLastTwoWeeks(1, "date_xxx"));
    }
}
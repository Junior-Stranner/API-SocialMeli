package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.entity.Post;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.mapper.PostMapper;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService - Testes Unitários")
class PostServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserFollowRepository followRepository;
    @Mock private PostMapper postMapper;
    @Mock private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl service;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private User seller;
    private User buyer;

    @BeforeEach
    void setup() {
        seller = new User("Seller", true);
        seller.setUserId(10L);

        buyer = new User("Buyer", false);
        buyer.setUserId(20L);
    }

    @Test
    @DisplayName("US-0005: Deve publicar post normal quando user existe e é seller")
    void publish_shouldSaveNormalPost_whenSellerExists() {
        PostPublishRequest req = mock(PostPublishRequest.class);
        when(req.getUserId()).thenReturn(10L);

        Post mapped = new Post(); // postId deve ficar null
        when(userRepository.findById(10L)).thenReturn(Optional.of(seller));
        when(postMapper.toEntity(req)).thenReturn(mapped);

        service.publish(req);

        verify(postRepository).save(postCaptor.capture());
        Post saved = postCaptor.getValue();

        assertNull(saved.getPostId());
        assertSame(seller, saved.getUser());
        assertFalse(saved.isHasPromo());
        assertEquals(0.0, saved.getDiscount());
    }

    @Test
    @DisplayName("US-0005: Deve lançar EntityNotFoundException quando user não existe (post normal)")
    void publish_shouldThrowEntityNotFound_whenUserDoesNotExist() {
        PostPublishRequest req = mock(PostPublishRequest.class);
        when(req.getUserId()).thenReturn(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.publish(req));

        verify(postMapper, never()).toEntity(any(PostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-0005: Deve lançar BusinessException quando user existe mas não é seller (post normal)")
    void publish_shouldThrowBusinessException_whenUserIsNotSeller() {
        PostPublishRequest req = mock(PostPublishRequest.class);
        when(req.getUserId()).thenReturn(20L);

        when(userRepository.findById(20L)).thenReturn(Optional.of(buyer));

        assertThrows(BusinessException.class, () -> service.publish(req));

        verify(postMapper, never()).toEntity(any(PostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Promo: Deve publicar post promo quando user é seller e discount é válido")
    void publishPromo_shouldSavePromoPost_whenSellerAndDiscountValid() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(10L);
        when(req.getDiscount()).thenReturn(0.25);

        Post mapped = new Post();
        when(userRepository.findById(10L)).thenReturn(Optional.of(seller));
        when(postMapper.toEntity(req)).thenReturn(mapped);

        service.publishPromo(req);

        verify(postRepository).save(postCaptor.capture());
        Post saved = postCaptor.getValue();

        assertNull(saved.getPostId());
        assertSame(seller, saved.getUser());
        assertTrue(saved.isHasPromo());
        assertEquals(0.25, saved.getDiscount());
    }


    @Test
    @DisplayName("Promo: Deve lançar BusinessException quando discount é null")
    void publishPromo_shouldThrowBusinessException_whenDiscountIsNull() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(10L);
        when(req.getDiscount()).thenReturn(null);

        when(userRepository.findById(10L)).thenReturn(Optional.of(seller));

        assertThrows(BusinessException.class, () -> service.publishPromo(req));

        verify(postMapper, never()).toEntity(any(PromoPostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Promo: Deve lançar BusinessException quando discount <= 0")
    void publishPromo_shouldThrowBusinessException_whenDiscountIsZeroOrLess() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(10L);
        when(req.getDiscount()).thenReturn(0.0);

        when(userRepository.findById(10L)).thenReturn(Optional.of(seller));

        assertThrows(BusinessException.class, () -> service.publishPromo(req));

        verify(postMapper, never()).toEntity(any(PromoPostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Promo: Deve lançar BusinessException quando discount > 100")
    void publishPromo_shouldThrowBusinessException_whenDiscountGreaterThan100() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(10L);
        when(req.getDiscount()).thenReturn(101.0);

        when(userRepository.findById(10L)).thenReturn(Optional.of(seller));

        assertThrows(BusinessException.class, () -> service.publishPromo(req));

        verify(postMapper, never()).toEntity(any(PromoPostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Promo: Deve lançar EntityNotFoundException quando user não existe")
    void publishPromo_shouldThrowEntityNotFound_whenUserDoesNotExist() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.publishPromo(req));

        verify(postMapper, never()).toEntity(any(PromoPostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Promo: Deve lançar BusinessException quando user existe mas não é seller")
    void publishPromo_shouldThrowBusinessException_whenUserIsNotSeller() {
        PromoPostPublishRequest req = mock(PromoPostPublishRequest.class);
        when(req.getUserId()).thenReturn(20L);

        when(userRepository.findById(20L)).thenReturn(Optional.of(buyer));

        assertThrows(BusinessException.class, () -> service.publishPromo(req));

        verify(postMapper, never()).toEntity(any(PromoPostPublishRequest.class));
        verify(postRepository, never()).save(any());
    }
}
package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.mapper.PostMapper;
import br.com.socialmedia.socialmedia.repository.PostRepository;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserFollowRepository followRepository;
    @Mock private PostMapper postMapper;
    @Mock private PostRepository postRepository;

    @InjectMocks private PostServiceImpl postService;

    // publish com buyer -> BusinessException
    @Test
    void publish_shouldThrowBusinessException_whenUserIsNotSeller() {
        // Arrange
        User buyer = new User("Buyer", false);
        buyer.setUserId(1L);

        PostPublishRequest req = new PostPublishRequest();
        req.setUserId(1l);

        when(userRepository.findById(1l)).thenReturn(Optional.of(buyer));

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.publish(req));
        verifyNoInteractions(postRepository);
    }

    // T-0005: order inválido em feed
    @Test
    void getFollowedPostsLastTwoWeeks_shouldThrowBusinessException_whenOrderInvalid() {
        // Arrange
        User buyer = new User("Buyer", false);
        buyer.setUserId(1l);

        User seller = new User("Seller", true);
        seller.setUserId(3l);

        when(userRepository.findById(1l)).thenReturn(Optional.of(buyer));
        when(followRepository.findFollowedSellers(1)).thenReturn(List.of(seller));
        when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), any(LocalDate.class)))
                .thenReturn(List.of()); // agora chega no sortPost

        // Act + Assert
        assertThrows(BusinessException.class,
                () -> postService.getFollowedPostsLastTwoWeeks(1, "date_xxx"));
    }

    // T-0008: garante que o "since" (now - 2 weeks) é usado no repository
    @Test
    void getFollowedPostsLastTwoWeeks_shouldUseSinceTwoWeeksAgo() {
        // Arrange
        User buyer = new User("Buyer", false);
        buyer.setUserId(1L);

        User seller = new User("Seller", true);
        seller.setUserId(3L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(followRepository.findFollowedSellers(1)).thenReturn(List.of(seller));

        when(postRepository.findByUserInAndDateAfterOrderByDateDesc(anySet(), any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        FollowedPostsResponse response = postService.getFollowedPostsLastTwoWeeks(1, "date_desc");

        // Assert
        assertNotNull(response);

        // Verifica que chamou o repo com a data correta (aproximada)
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        verify(postRepository).findByUserInAndDateAfterOrderByDateDesc(anySet(), dateCaptor.capture());

        LocalDate expectedSince = LocalDate.now().minusWeeks(2);
        assertEquals(expectedSince, dateCaptor.getValue());
    }

    //promo discount inválido
    @Test
    void publishPromo_shouldThrowBusinessException_whenDiscountInvalid() {
        // Arrange
        User seller = new User("Seller", true);
        seller.setUserId(3l);

        PromoPostPublishRequest req = new PromoPostPublishRequest();
        req.setUserId(3l);
        req.setHasPromo(true);
        req.setDiscount(200.0);

        when(userRepository.findById(3l)).thenReturn(Optional.of(seller));

        // Act + Assert
        assertThrows(BusinessException.class, () -> postService.publishPromo(req));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(postMapper);
    }
}
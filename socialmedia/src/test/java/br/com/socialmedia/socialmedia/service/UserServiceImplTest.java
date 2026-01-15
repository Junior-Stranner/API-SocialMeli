package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserFollowRepository followRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    private User buyer;
    private User seller;

    @BeforeEach
    void setup() {
        buyer = new User("Buyer", false);
        buyer.setUserId(1L);

        seller = new User("Seller", true);
        seller.setUserId(3L);
    }

    // T-0001 (US-0001): usuário a seguir existe?
    @Test
    void follow_shouldThrowNotFound_whenSellerDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> userService.follow(1, 99));
        verifyNoInteractions(followRepository);
    }

    // T-0002 (US-0007): usuário a deixar de seguir existe?
    @Test
    void unfollow_shouldThrowNotFound_whenSellerDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> userService.unfollow(1, 99));
        verifyNoInteractions(followRepository);
    }

    // regra essencial: follow duplicado -> Conflict
    @Test
    void follow_shouldThrowConflict_whenAlreadyFollowing() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(1, 3)).thenReturn(true);

        // Act + Assert
        assertThrows(ConflictException.class, () -> userService.follow(1, 3));
        verify(followRepository, never()).save(any());
    }

    // T-0007 (US-0002): contagem correta
    @Test
    void getFollowersCount_shouldReturnCorrectCount() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(seller));
        when(followRepository.countBySellerId(3)).thenReturn(5L);

        // Act
        FollowersCountDto result = userService.getFollowersCount(3);

        // Assert
        assertEquals(5, result.followersCount());
    }

    //(T-0003 / US-0008): order inválida
    @Test
    void getFollowersList_shouldThrowBusinessException_whenOrderInvalid() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(seller));

        // Act + Assert
        assertThrows(BusinessException.class, () -> userService.getFollowersList(3, "invalid"));
    }
}
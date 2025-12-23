package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void follow_success_whenBuyerFollowsSeller() {
        // Arrange
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Junior", false);
        buyer.setId(buyerId);

        User seller = new User("Julia", true);
        seller.setId(sellerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(userMapper.toDto(any(User.class))).thenReturn(null);

        // Act
        userService.follow(buyerId, sellerId);

        // Assert
        verify(userRepository).save(seller);
        assertTrue(seller.isFollowedBy(buyer));
    }

    @Test
    void follow_shouldThrowBusinessException_whenUserFollowsSelf() {
        assertThrows(BusinessException.class, () -> userService.follow(1, 1));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void follow_shouldThrowBusinessException_whenTargetIsNotSeller() {
        int buyerId = 1;
        int targetId = 2;

        User buyer = new User("Junior", false);
        buyer.setId(buyerId);

        User target = new User("Alice", false);
        target.setId(targetId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

        assertThrows(BusinessException.class, () -> userService.follow(buyerId, targetId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void follow_shouldThrowConflictException_whenAlreadyFollowing() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Junior", false);
        buyer.setId(buyerId);

        User seller = new User("José", true);
        seller.setId(sellerId);

        seller.addFollower(buyer); // já segue

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        assertThrows(ConflictException.class, () -> userService.follow(buyerId, sellerId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void unfollow_success_whenFollowing() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Junior", false);
        buyer.setId(buyerId);

        User seller = new User("José", true);
        seller.setId(sellerId);

        seller.addFollower(buyer);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(userMapper.toDto(any(User.class))).thenReturn(null);

        userService.unfollow(buyerId, sellerId);

        verify(userRepository).save(seller);
        assertFalse(buyer.isFollowing(seller));
    }

    @Test
    void unfollow_shouldThrowConflictException_whenNotFollowing() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Junior", false);
        buyer.setId(buyerId);

        User seller = new User("José", true);
        seller.setId(sellerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        assertThrows(ConflictException.class, () -> userService.unfollow(buyerId, sellerId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void follow_shouldThrowNotFound_whenBuyerDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.follow(1, 2));
    }
}
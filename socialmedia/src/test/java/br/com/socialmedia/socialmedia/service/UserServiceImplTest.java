package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFollowRepository followRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * T-0001 (US-0001)
     * Verificar se o usuário a ser seguido existe.
     * - Se não existe: lança exceção (EntityNotFoundException)
     */
    @Test
    void T0001_follow_shouldThrowNotFound_whenUserToFollowDoesNotExist() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.follow(buyerId, sellerId));

        verifyNoInteractions(followRepository);
        verifyNoInteractions(userMapper);
    }

    /**
     * Caso "cumprida" do T-0001:
     * usuário existe -> segue normalmente (não precisa validar retorno aqui, só que salva follow)
     */
    @Test
    void follow_shouldFollow_whenUserToFollowExists() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        User seller = new User("Seller", true);
        seller.setId(sellerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(buyerId, sellerId)).thenReturn(false);

        userService.follow(buyerId, sellerId);

        verify(followRepository).save(any(UserFollow.class));
    }

    /**
     * T-0002 (US-0007)
     * Verificar se o usuário a ser deixado de seguir existe.
     * - Se não existe: lança EntityNotFoundException
     */
    @Test
    void T0002_unfollow_shouldThrowNotFound_whenUserToUnfollowDoesNotExist() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.unfollow(buyerId, sellerId));

        verifyNoInteractions(followRepository);
        verifyNoInteractions(userMapper);
    }

    /**
     * Extra essencial (regra de negócio): follow self -> BusinessException
     */
    @Test
    void follow_shouldThrowBusinessException_whenUserFollowsSelf() {
        assertThrows(BusinessException.class, () -> userService.follow(1, 1));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(followRepository);
    }

    /**
     * Extra essencial (regra de negócio): target não é seller -> BusinessException
     */
    @Test
    void follow_shouldThrowBusinessException_whenTargetIsNotSeller() {
        int buyerId = 1;
        int targetId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        User target = new User("NotSeller", false);
        target.setId(targetId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

        assertThrows(BusinessException.class, () -> userService.follow(buyerId, targetId));
        verifyNoInteractions(followRepository);
    }

    /**
     * Extra essencial (regra de negócio): follow duplicado -> ConflictException
     */
    @Test
    void follow_shouldThrowConflictException_whenAlreadyFollowing() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        User seller = new User("Seller", true);
        seller.setId(sellerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(buyerId, sellerId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.follow(buyerId, sellerId));
        verify(followRepository, never()).save(any());
    }

    /**
     * Extra essencial (regra de negócio): unfollow sem seguir -> ConflictException
     */
    @Test
    void unfollow_shouldThrowConflictException_whenNotFollowing() {
        int buyerId = 1;
        int sellerId = 2;

        User buyer = new User("Buyer", false);
        buyer.setId(buyerId);

        User seller = new User("Seller", true);
        seller.setId(sellerId);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(buyerId, sellerId)).thenReturn(false);

        assertThrows(ConflictException.class, () -> userService.unfollow(buyerId, sellerId));
        verify(followRepository, never()).deleteByFollowerIdAndSellerId(anyInt(), anyInt());
    }
}
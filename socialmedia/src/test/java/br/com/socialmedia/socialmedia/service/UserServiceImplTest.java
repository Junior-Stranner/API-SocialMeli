package br.com.socialmedia.socialmedia.service;

import br.com.socialmedia.socialmedia.dto.FollowDto;
import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.entity.UserFollow;
import br.com.socialmedia.socialmedia.exception.BusinessException;
import br.com.socialmedia.socialmedia.exception.ConflictException;
import br.com.socialmedia.socialmedia.mapper.UserMapper;
import br.com.socialmedia.socialmedia.repository.UserFollowRepository;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import br.com.socialmedia.socialmedia.service.serviceImpl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;  // ← Import correto
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Testes Unitários")
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserFollowRepository followRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    private User buyer1;
    private User buyer2;
    private User seller;

    @BeforeEach
    void setUp() {
        buyer1 = createUser(1, "Buyer1", false);
        buyer2 = createUser(2, "Buyer2", false);
        seller = createUser(3, "Seller", true);
    }

    // ==================================================================================
    // US-0001: Seguir um vendedor (T-0001)
    // ==================================================================================

    @Test
    @DisplayName("T-0001: Deve lançar EntityNotFoundException quando usuário a seguir não existe")
    void follow_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.follow(1, 99));
        verifyNoInteractions(followRepository);
    }

    @Test
    @DisplayName("T-0001: Deve seguir normalmente quando usuário existe e é vendedor")
    void follow_shouldSave_whenUserExistsAndIsSeller() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(1, 3)).thenReturn(false);

        userService.follow(1, 3);

        verify(followRepository).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário tenta seguir a si mesmo")
    void follow_shouldThrowBusinessException_whenFollowingSelf() {
        assertThrows(BusinessException.class, () -> userService.follow(1, 1));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(followRepository);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário a seguir não é vendedor")
    void follow_shouldThrowBusinessException_whenTargetIsNotSeller() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(2)).thenReturn(Optional.of(buyer2));

        assertThrows(BusinessException.class, () -> userService.follow(1, 2));
        verifyNoInteractions(followRepository);
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando já está seguindo")
    void follow_shouldThrowConflictException_whenAlreadyFollowing() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(1, 3)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.follow(1, 3));
        verify(followRepository, never()).save(any());
    }

    // ==================================================================================
    // US-0007: Deixar de seguir um vendedor (T-0002)
    // ==================================================================================

    @Test
    @DisplayName("T-0002: Deve lançar EntityNotFoundException quando usuário a deixar de seguir não existe")
    void unfollow_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.unfollow(1, 99));
        verifyNoInteractions(followRepository);
    }

    @Test
    @DisplayName("T-0002: Deve deixar de seguir normalmente quando relacionamento existe")
    void unfollow_shouldDelete_whenFollowExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(1, 3)).thenReturn(true);

        userService.unfollow(1, 3);

        verify(followRepository).deleteByFollowerIdAndSellerId(1, 3);
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando não está seguindo")
    void unfollow_shouldThrowConflictException_whenNotFollowing() {
        when(userRepository.findById(1)).thenReturn(Optional.of(buyer1));
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.existsByFollowerIdAndSellerId(1, 3)).thenReturn(false);

        assertThrows(ConflictException.class, () -> userService.unfollow(1, 3));
        verify(followRepository, never()).deleteByFollowerIdAndSellerId(anyInt(), anyInt());
    }

    // ==================================================================================
    // Método auxiliar
    // ==================================================================================

    private User createUser(int id, String name, boolean isSeller) {
        User user = new User(name, isSeller);
        user.setUserId(id);
        return user;
    }

    // ==================================================================================
// US-0002: Contagem de seguidores (T-0007)
// ==================================================================================

    @Test
    @DisplayName("T-0007: Deve retornar contagem correta de seguidores")
    void getFollowersCount_shouldReturnCorrectCount() {
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.countBySellerId(3)).thenReturn(5L);

        var result = userService.getFollowersCount(3);

        assertEquals(5, result.followersCount());
    }

// ==================================================================================
// US-0008: Ordenação alfabética (T-0003, T-0004)
// ==================================================================================

    @Test
    @DisplayName("T-0003: Deve lançar exceção quando ordenação é inválida")
    void getFollowersList_shouldThrowException_whenInvalidOrder() {
        when(userRepository.findById(3)).thenReturn(Optional.of(seller));

        assertThrows(BusinessException.class, () -> userService.getFollowersList(3, "invalid"));
    }

    @Test
    @DisplayName("T-0004: Deve ordenar por nome ASC corretamente")
    void getFollowersList_shouldOrderByNameAsc() {
        User userA = createUser(10, "Ana", false);
        User userB = createUser(11, "Bruno", false);

        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.findFollowersOrderByNameAsc(3)).thenReturn(List.of(userA, userB));
        when(userMapper.toFollowList(any())).thenReturn(List.of(
                new FollowDto(10, "Ana"),
                new FollowDto(11, "Bruno")
        ));

        var result = userService.getFollowersList(3, "name_asc");

        assertEquals("Ana", result.followers().get(0).userName());
        assertEquals("Bruno", result.followers().get(1).userName());
    }

    @Test
    @DisplayName("T-0004: Deve ordenar por nome DESC corretamente")
    void getFollowersList_shouldOrderByNameDesc() {
        User userA = createUser(10, "Ana", false);
        User userB = createUser(11, "Bruno", false);

        when(userRepository.findById(3)).thenReturn(Optional.of(seller));
        when(followRepository.findFollowersOrderByNameDesc(3)).thenReturn(List.of(userB, userA));
        when(userMapper.toFollowList(any())).thenReturn(List.of(
                new FollowDto(11, "Bruno"),
                new FollowDto(10, "Ana")
        ));

        var result = userService.getFollowersList(3, "name_desc");

        assertEquals("Bruno", result.followers().get(0).userName());
        assertEquals("Ana", result.followers().get(1).userName());
    }

}
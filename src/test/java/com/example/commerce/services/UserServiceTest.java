package com.example.Commerce.services;

import com.example.Commerce.dtos.LoginDTO;
import com.example.Commerce.dtos.LoginResponseDTO;
import com.example.Commerce.entities.UserEntity;
import com.example.Commerce.mappers.UserMapper;
import com.example.Commerce.repositories.UserRepository;
import com.example.Commerce.errorhandlers.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void loginUser_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(email);
        userEntity.setPassword(hashedPassword);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        LoginResponseDTO expectedResponse = new LoginResponseDTO();
        expectedResponse.setId(userId);
        expectedResponse.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponseDTO(userEntity)).thenReturn(expectedResponse);

        // Act
        LoginResponseDTO actualResponse = userService.loginUser(loginDTO);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getToken());
        assertTrue(actualResponse.getToken().endsWith("-" + userId));
        assertEquals(email, actualResponse.getEmail());
        verify(userRepository).findByEmail(email);
        verify(userMapper).toResponseDTO(userEntity);
    }

    @Test
    void loginUser_InvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(hashedPassword);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(wrongPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void loginUser_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.loginUser(loginDTO));
    }
}

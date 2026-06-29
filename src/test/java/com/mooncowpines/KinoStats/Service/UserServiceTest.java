package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mooncowpines.KinoStats.DTO.UserPasswordUpdateDTO;
import com.mooncowpines.KinoStats.Exceptions.InvalidPasswordException;
import com.mooncowpines.KinoStats.Exceptions.ValidationException;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MovieListService movieListService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@kino.com");
        existingUser.setPassword("$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("CP-01: Registro válido persiste usuario con password hasheado y crea watchlist")
    void addUser_validData_savesUserAndCreatesWatchlist() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@kino.com");
        newUser.setPassword("plainPassword");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@kino.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.addUser(newUser);

        assertThat(newUser.getPassword()).isEqualTo("$2a$10$hashed");
        verify(userRepository).save(newUser);
        verify(movieListService).createWatchlist(newUser);
    }

    @Test
    @DisplayName("CP-02: Registro con username duplicado lanza ValidationException")
    void addUser_duplicateUsername_throwsValidationException() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("new@kino.com");
        newUser.setPassword("secret123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.addUser(newUser))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Username already taken");

        verify(userRepository, never()).save(any());
        verify(movieListService, never()).createWatchlist(any());
    }

    @Test
    @DisplayName("CP-03: Registro con email duplicado lanza ValidationException")
    void addUser_duplicateEmail_throwsValidationException() {
        User newUser = new User();
        newUser.setUsername("unique");
        newUser.setEmail("test@kino.com");
        newUser.setPassword("secret123");

        when(userRepository.findByUsername("unique")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@kino.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.addUser(newUser))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-11: Cambio de password con oldPassword correcta actualiza password")
    void updatePassword_correctOldPassword_savesNewHashedPassword() {
        UserPasswordUpdateDTO dto = new UserPasswordUpdateDTO("newSecret123", "password123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newSecret123")).thenReturn("$2a$10$newHashed");

        userService.updatePassword(1L, dto);

        assertThat(existingUser.getPassword()).isEqualTo("$2a$10$newHashed");
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("CP-12: Cambio de password con oldPassword incorrecta lanza InvalidPasswordException")
    void updatePassword_wrongOldPassword_throwsInvalidPasswordException() {
        UserPasswordUpdateDTO dto = new UserPasswordUpdateDTO("newSecret123", "wrong");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "$2a$10$hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(1L, dto))
            .isInstanceOf(InvalidPasswordException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-25: Tras registro de usuario nuevo se invoca creación de watchlist")
    void addUser_newUser_triggersWatchlistCreation() {
        User newUser = new User();
        newUser.setUsername("freshuser");
        newUser.setEmail("fresh@kino.com");
        newUser.setPassword("secret123");

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.addUser(newUser);

        verify(movieListService, times(1)).createWatchlist(newUser);
    }
}

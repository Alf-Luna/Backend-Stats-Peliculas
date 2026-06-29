package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mooncowpines.KinoStats.Exceptions.ValidationException;
import com.mooncowpines.KinoStats.Model.PasswordReset;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.PasswordResetRepository;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private PasswordResetRepository resetRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks private PasswordResetService passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@kino.com");
        user.setPassword("$2a$10$oldHash");
    }

    @Test
    @DisplayName("CP-13: requestReset con email válido genera código y envía email")
    void requestReset_validEmail_savesCodeAndSendsEmail() {
        when(userRepository.findByEmail("test@kino.com")).thenReturn(Optional.of(user));

        passwordResetService.requestReset("test@kino.com");

        ArgumentCaptor<PasswordReset> captor = ArgumentCaptor.forClass(PasswordReset.class);
        verify(resetRepository).save(captor.capture());

        PasswordReset saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getCode()).hasSize(6);
        assertThat(saved.getCode()).matches("\\d{6}");
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(14));
        assertThat(saved.getExpiresAt()).isBefore(LocalDateTime.now().plusMinutes(16));

        verify(emailService).sendResetCode(eq("test@kino.com"), anyString());
    }

    @Test
    @DisplayName("CP-14: requestReset con email inexistente lanza EntityNotFoundException")
    void requestReset_nonExistentEmail_throwsEntityNotFoundException() {
        when(userRepository.findByEmail("nada@kino.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.requestReset("nada@kino.com"))
            .isInstanceOf(EntityNotFoundException.class);

        verify(resetRepository, never()).save(any());
        verify(emailService, never()).sendResetCode(any(), any());
    }

    @Test
    @DisplayName("CP-15: resetPassword con código válido actualiza password y marca código como usado")
    void resetPassword_validCode_updatesPasswordAndMarksUsed() {
        PasswordReset reset = new PasswordReset();
        reset.setId(1L);
        reset.setCode("123456");
        reset.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        reset.setUsed(false);
        reset.setUser(user);

        when(userRepository.findByEmail("test@kino.com")).thenReturn(Optional.of(user));
        when(resetRepository.findByUserIdAndCodeAndUsedFalse(1L, "123456")).thenReturn(Optional.of(reset));
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHash");

        passwordResetService.resetPassword("test@kino.com", "123456", "newPassword123");

        assertThat(reset.getUsed()).isTrue();
        assertThat(user.getPassword()).isEqualTo("$2a$10$newHash");
        verify(resetRepository).save(reset);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("CP-16: resetPassword con código inválido lanza ValidationException")
    void resetPassword_invalidCode_throwsValidationException() {
        when(userRepository.findByEmail("test@kino.com")).thenReturn(Optional.of(user));
        when(resetRepository.findByUserIdAndCodeAndUsedFalse(1L, "999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword("test@kino.com", "999999", "newPassword123"))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Invalid code");

        verify(userRepository, never()).save(any());
    }
}
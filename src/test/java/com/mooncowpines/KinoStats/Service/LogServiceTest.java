package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import com.mooncowpines.KinoStats.DTO.LogDTO;
import com.mooncowpines.KinoStats.DTO.LogRequestDTO;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.LogRepository;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock private LogRepository logRepository;
    @Mock private FilmService filmService;
    @Mock private UserRepository userRepository;

    @InjectMocks private LogService logService;

    private User user;
    private Film film;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        film = new Film();
        film.setId(550L);
        film.setTitle("Fight Club");
    }

    private LogRequestDTO buildRequest(Float rating) {
        LogRequestDTO req = new LogRequestDTO();
        req.setDate(LocalDate.of(2025, 5, 10));
        req.setReview("Great movie");
        req.setRating(rating);
        req.setFilmId(550L);
        req.setUserId(1L);
        req.setFirstWatch(true);
        return req;
    }

    @Test
    @DisplayName("CP-17: Crear log válido persiste el log con todos los campos")
    void addLog_validRequest_savesLog() {
        LogRequestDTO req = buildRequest(4.0f);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(filmService.findOrFetchAndSave(550L)).thenReturn(film);

        logService.addLog(req);

        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository).save(captor.capture());
        Log saved = captor.getValue();

        assertThat(saved.getRating()).isEqualTo(4.0f);
        assertThat(saved.getReview()).isEqualTo("Great movie");
        assertThat(saved.getFilm()).isEqualTo(film);
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getFirstWatch()).isTrue();
    }

    @Test
    @DisplayName("CP-18: Rating mayor a 5.0 se ajusta (clamp) a 5.0")
    void addLog_ratingAboveMax_clampsToFive() {
        LogRequestDTO req = buildRequest(8.0f);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(filmService.findOrFetchAndSave(550L)).thenReturn(film);

        logService.addLog(req);

        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository).save(captor.capture());
        assertThat(captor.getValue().getRating()).isEqualTo(5.0f);
    }

    @Test
    @DisplayName("CP-19: Rating menor a 0.5 se ajusta (clamp) a 0.5")
    void addLog_ratingBelowMin_clampsToHalf() {
        LogRequestDTO req = buildRequest(0.1f);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(filmService.findOrFetchAndSave(550L)).thenReturn(film);

        logService.addLog(req);

        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository).save(captor.capture());
        assertThat(captor.getValue().getRating()).isEqualTo(0.5f);
    }

    @Test
    @DisplayName("CP-20: Crear log con userId inexistente lanza EntityNotFoundException")
    void addLog_userNotFound_throwsEntityNotFoundException() {
        LogRequestDTO req = buildRequest(4.0f);
        req.setUserId(9999L);

        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logService.addLog(req))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("User not found");

        verify(logRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-21: Listar logs por usuario devuelve DTOs en orden descendente por fecha")
    void getLogsByUserId_returnsDtosInDescendingDateOrder() {
        Film fightClub = new Film();
        fightClub.setId(550L);
        fightClub.setTitle("Fight Club");
        fightClub.setReleaseYear(1999);
        fightClub.setPosterPath("/fc.jpg");

        Film spirited = new Film();
        spirited.setId(129L);
        spirited.setTitle("Spirited Away");
        spirited.setReleaseYear(2001);
        spirited.setPosterPath("/sa.jpg");

        Log recentLog = new Log();
        recentLog.setId(1L);
        recentLog.setDate(LocalDate.of(2025, 4, 20));
        recentLog.setRating(5.0f);
        recentLog.setReview("Classic");
        recentLog.setFirstWatch(true);
        recentLog.setFilm(fightClub);
        recentLog.setUser(user);

        Log olderLog = new Log();
        olderLog.setId(2L);
        olderLog.setDate(LocalDate.of(2025, 3, 10));
        olderLog.setRating(4.5f);
        olderLog.setReview("Great");
        olderLog.setFirstWatch(true);
        olderLog.setFilm(spirited);
        olderLog.setUser(user);

        when(logRepository.findByUserIdOrderByDateDesc(1L))
            .thenReturn(List.of(recentLog, olderLog));

        List<LogDTO> result = logService.getLogsByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2025, 4, 20));
        assertThat(result.get(0).title()).isEqualTo("Fight Club");
        assertThat(result.get(1).date()).isEqualTo(LocalDate.of(2025, 3, 10));
        assertThat(result.get(1).title()).isEqualTo("Spirited Away");
    }

    @Test
    @DisplayName("CP-22: Actualizar log existente modifica los campos y guarda")
    void updateLog_existingLog_savesUpdatedFields() {
        Log existing = new Log();
        existing.setId(1L);
        existing.setDate(LocalDate.of(2025, 3, 10));
        existing.setRating(3.0f);
        existing.setReview("Old review");
        existing.setFirstWatch(false);

        LogRequestDTO req = buildRequest(4.5f);

        when(logRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(logRepository.save(any(Log.class))).thenAnswer(inv -> inv.getArgument(0));

        Log updated = logService.updateLog(1L, req);

        assertThat(updated.getRating()).isEqualTo(4.5f);
        assertThat(updated.getReview()).isEqualTo("Great movie");
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2025, 5, 10));
        assertThat(updated.getFirstWatch()).isTrue();
        verify(logRepository).save(existing);
    }

    @Test
    @DisplayName("CP-23: Eliminar log invoca deleteById en el repository")
    void deleteLog_validId_invokesRepositoryDelete() {
        logService.deleteLog(1L);
        verify(logRepository).deleteById(1L);
    }
}
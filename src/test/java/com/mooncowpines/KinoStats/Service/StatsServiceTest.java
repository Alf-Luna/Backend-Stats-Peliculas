package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mooncowpines.KinoStats.DTO.StatsRequestDTO;
import com.mooncowpines.KinoStats.DTO.StatsResponseDTO;
import com.mooncowpines.KinoStats.Repository.LogRepository;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private LogRepository logRepository;

    @InjectMocks private StatsService statsService;

    @Test
    @DisplayName("CP-30: Stats anuales usan rango 1 de enero a 31 de diciembre")
    void calculateStats_yearOnly_usesFullYearRange() {
        StatsRequestDTO req = new StatsRequestDTO(1L, null, 2025);

        LocalDate startExpected = LocalDate.of(2025, 1, 1);
        LocalDate endExpected = LocalDate.of(2025, 12, 31);

        when(logRepository.countMoviesWatched(eq(1L), eq(startExpected), eq(endExpected))).thenReturn(10);
        when(logRepository.sumWatchTime(eq(1L), eq(startExpected), eq(endExpected))).thenReturn(600);
        when(logRepository.watchesByGenre(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByCountry(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByPerson(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.countsByRating(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByDecade(any(), any(), any())).thenReturn(Collections.emptyList());

        StatsResponseDTO result = statsService.calculateStats(req);

        assertThat(result.moviesWatched()).isEqualTo(10);
        assertThat(result.minutesWatched()).isEqualTo(600);
        assertThat(result.hoursWatched()).isEqualTo(10);
    }

    @Test
    @DisplayName("CP-31: Stats mensuales usan rango del primer al último día del mes")
    void calculateStats_withMonth_usesMonthRange() {
        StatsRequestDTO req = new StatsRequestDTO(1L, 3, 2025);

        LocalDate startExpected = LocalDate.of(2025, 3, 1);
        LocalDate endExpected = LocalDate.of(2025, 3, 31);

        when(logRepository.countMoviesWatched(eq(1L), eq(startExpected), eq(endExpected))).thenReturn(3);
        when(logRepository.sumWatchTime(any(), any(), any())).thenReturn(300);
        when(logRepository.watchesByGenre(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByCountry(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByPerson(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.countsByRating(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByDecade(any(), any(), any())).thenReturn(Collections.emptyList());

        StatsResponseDTO result = statsService.calculateStats(req);

        assertThat(result.moviesWatched()).isEqualTo(3);
    }

    @Test
    @DisplayName("CP-32: Stats con usuario sin logs devuelve conteos en 0 y listas vacías")
    void calculateStats_noLogs_returnsZeroAndEmptyLists() {
        StatsRequestDTO req = new StatsRequestDTO(1L, null, 2030);

        when(logRepository.countMoviesWatched(any(), any(), any())).thenReturn(0);
        when(logRepository.sumWatchTime(any(), any(), any())).thenReturn(null);
        when(logRepository.watchesByGenre(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByCountry(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByPerson(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.countsByRating(any(), any(), any())).thenReturn(Collections.emptyList());
        when(logRepository.watchesByDecade(any(), any(), any())).thenReturn(Collections.emptyList());

        StatsResponseDTO result = statsService.calculateStats(req);

        assertThat(result.moviesWatched()).isZero();
        assertThat(result.hoursWatched()).isZero();
        assertThat(result.moviesWatchedByGenre()).isEmpty();
        assertThat(result.moviesWatchedByCountry()).isEmpty();
    }
}
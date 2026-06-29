package com.mooncowpines.KinoStats.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class LogRepositoryTest {

    @Autowired private LogRepository logRepository;

    @Test
    void cp40_countMoviesWatched_devuelveConteoCorrecto() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        Integer count = logRepository.countMoviesWatched(1L, start, end);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countMoviesWatched_periodoSinLogs_devuelveCero() {
        LocalDate start = LocalDate.of(2030, 1, 1);
        LocalDate end = LocalDate.of(2030, 12, 31);

        Integer count = logRepository.countMoviesWatched(1L, start, end);

        assertThat(count).isZero();
    }

    @Test
    void sumWatchTime_periodoConLogs_devuelveSumaCorrecta() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        Integer sum = logRepository.sumWatchTime(1L, start, end);

        // 125+139
        assertThat(sum).isEqualTo(264);
    }
}
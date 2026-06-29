package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mooncowpines.KinoStats.DTO.HomeDTO;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Model.MovieList;
import com.mooncowpines.KinoStats.Model.User;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    @Mock private MovieListService movieListService;
    @Mock private LogService logService;

    @InjectMocks private HomeService homeService;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        film = new Film();
        film.setId(550L);
        film.setTitle("Fight Club");
        film.setReleaseYear(1999);
        film.setLengthInMinutes(139);
        film.setPosterPath("/poster1.jpg");
    }

    @Test
    @DisplayName("CP-33: Home de usuario con actividad devuelve watchlist, lastSeen y justWatched")
    void getHomeData_userWithActivity_returnsPopulatedHome() {
        Log log = new Log();
        log.setId(1L);
        log.setDate(LocalDate.of(2025, 5, 10));
        log.setFilm(film);
        log.setUser(user);

        MovieList watchlist = new MovieList();
        watchlist.setId(1L);
        watchlist.setName("Watchlist");
        watchlist.setIsWatchlist(true);
        Set<Film> films = new HashSet<>();
        films.add(film);
        watchlist.setFilms(films);

        when(movieListService.getWatchList(1L)).thenReturn(Optional.of(watchlist));
        when(logService.findLatestLogs(1L)).thenReturn(List.of(log));

        HomeDTO result = homeService.getHomeData(1L);

        assertThat(result.lastSeen()).isNotNull();
        assertThat(result.lastSeen().id()).isEqualTo(550L);
        assertThat(result.justWatched()).hasSize(1);
        assertThat(result.watchList()).hasSize(1);
    }

    @Test
    @DisplayName("CP-34: Home de usuario sin logs devuelve lastSeen y justWatched null")
    void getHomeData_userWithoutLogs_returnsNullValues() {
        when(movieListService.getWatchList(1L)).thenReturn(Optional.empty());
        when(logService.findLatestLogs(1L)).thenReturn(Collections.emptyList());

        HomeDTO result = homeService.getHomeData(1L);

        assertThat(result.lastSeen()).isNull();
        assertThat(result.justWatched()).isNull();
        assertThat(result.watchList()).isNull();
    }
}
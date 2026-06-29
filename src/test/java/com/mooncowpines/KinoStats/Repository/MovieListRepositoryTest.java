package com.mooncowpines.KinoStats.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.mooncowpines.KinoStats.Model.MovieList;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class MovieListRepositoryTest {

    @Autowired private MovieListRepository movieListRepository;

    @Test
    void cp41_findByUserIdAndIsWatchlistTrue_devuelveWatchlistDelUsuario() {
        Optional<MovieList> result = movieListRepository.findByUserIdAndIsWatchlistTrue(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getIsWatchlist()).isTrue();
        assertThat(result.get().getName()).isEqualTo("Watchlist");
        assertThat(result.get().getUser().getId()).isEqualTo(1L);
    }

    @Test
    void findByUserIdAndIsWatchlistTrue_usuarioSinWatchlist_devuelveEmpty() {
        Optional<MovieList> result = movieListRepository.findByUserIdAndIsWatchlistTrue(999L);

        assertThat(result).isEmpty();
    }
}
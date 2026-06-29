package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mooncowpines.KinoStats.DTO.MovieListAddDTO;
import com.mooncowpines.KinoStats.DTO.MovieListDTO;
import com.mooncowpines.KinoStats.DTO.MovieListRequestDTO;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.MovieList;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.MovieListRepository;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class MovieListServiceTest {

    @Mock private MovieListRepository movieListRepository;
    @Mock private UserRepository userRepository;
    @Mock private FilmService filmService;

    @InjectMocks private MovieListService movieListService;

    private User user;
    private MovieList list;
    private Film film;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        list = new MovieList();
        list.setId(1L);
        list.setName("Favoritos");
        list.setIsWatchlist(false);
        list.setUser(user);
        list.setFilms(new HashSet<>());

        film = new Film();
        film.setId(550L);
        film.setTitle("Fight Club");
    }

    @Test
    @DisplayName("CP-24: Crear lista personalizada persiste con isWatchlist=false")
    void createList_validRequest_savesNonWatchlistList() {
        MovieListRequestDTO req = new MovieListRequestDTO(1L, "Favoritos");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(movieListRepository.save(any(MovieList.class))).thenAnswer(inv -> {
            MovieList l = inv.getArgument(0);
            l.setId(5L);
            return l;
        });

        MovieListDTO result = movieListService.createList(req);

        assertThat(result.name()).isEqualTo("Favoritos");
        assertThat(result.isWatchlist()).isFalse();

        ArgumentCaptor<MovieList> captor = ArgumentCaptor.forClass(MovieList.class);
        verify(movieListRepository).save(captor.capture());
        assertThat(captor.getValue().getIsWatchlist()).isFalse();
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("CP-25: createWatchlist crea una lista con isWatchlist=true para el usuario")
    void createWatchlist_validUser_savesListMarkedAsWatchlist() {
        movieListService.createWatchlist(user);

        ArgumentCaptor<MovieList> captor = ArgumentCaptor.forClass(MovieList.class);
        verify(movieListRepository).save(captor.capture());

        MovieList saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Watchlist");
        assertThat(saved.getIsWatchlist()).isTrue();
        assertThat(saved.getUser()).isEqualTo(user);
    }

@Test
@DisplayName("CP-26: Obtener listas de un usuario devuelve todas mapeadas a DTOs")
void getListsByUser_returnsAllUserListsAsDtos() {
    MovieList watchlist = new MovieList();
    watchlist.setId(1L);
    watchlist.setName("Watchlist");
    watchlist.setIsWatchlist(true);
    watchlist.setUser(user);
    watchlist.setFilms(new HashSet<>());

    MovieList favorites = new MovieList();
    favorites.setId(2L);
    favorites.setName("Favoritos");
    favorites.setIsWatchlist(false);
    favorites.setUser(user);
    Set<Film> favFilms = new HashSet<>();
    favFilms.add(film);
    favorites.setFilms(favFilms);

    when(movieListRepository.findByUserId(1L))
        .thenReturn(List.of(watchlist, favorites));

    List<MovieListDTO> result = movieListService.getListsByUser(1L);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).name()).isEqualTo("Watchlist");
    assertThat(result.get(0).isWatchlist()).isTrue();
    assertThat(result.get(0).movieCount()).isZero();
    assertThat(result.get(1).name()).isEqualTo("Favoritos");
    assertThat(result.get(1).isWatchlist()).isFalse();
    assertThat(result.get(1).movieCount()).isEqualTo(1);
    assertThat(result.get(1).movies()).hasSize(1);
    assertThat(result.get(1).movies().get(0).id()).isEqualTo(550L);
}

    @Test
    @DisplayName("CP-27: Agregar película existente a lista la asocia correctamente")
    void addFilmToList_existingFilm_associatesFilm() {
        MovieListAddDTO req = new MovieListAddDTO(1L, 1L, 550L);

        when(movieListRepository.findById(1L)).thenReturn(Optional.of(list));
        when(filmService.findOrFetchAndSave(550L)).thenReturn(film);

        movieListService.addFilmToList(req);

        assertThat(list.getFilms()).contains(film);
        verify(movieListRepository).save(list);
    }

    @Test
    @DisplayName("CP-28: Quitar película de lista la remueve")
    void removeFilmFromList_filmInList_removesFilm() {
        Set<Film> films = new HashSet<>();
        films.add(film);
        list.setFilms(films);

        when(movieListRepository.findById(1L)).thenReturn(Optional.of(list));

        movieListService.removeFilmFromList(1L, 550L);

        assertThat(list.getFilms()).doesNotContain(film);
        verify(movieListRepository).save(list);
    }

    @Test
    @DisplayName("CP-29: Eliminar lista inexistente lanza EntityNotFoundException")
    void deleteList_nonExistent_throwsEntityNotFoundException() {
        when(movieListRepository.existsById(9999L)).thenReturn(false);

        assertThatThrownBy(() -> movieListService.deleteList(9999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("List not found");

        verify(movieListRepository, never()).deleteById(any());
    }
}
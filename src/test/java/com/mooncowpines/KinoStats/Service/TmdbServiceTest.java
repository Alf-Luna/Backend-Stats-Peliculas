package com.mooncowpines.KinoStats.Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mooncowpines.KinoStats.DTO.MovieDetailsDTO;
import com.mooncowpines.KinoStats.DTO.TmdbMovieSearchResponse;

class TmdbServiceTest {

    private MockWebServer mockWebServer;
    private TmdbService tmdbService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        baseUrl = baseUrl.substring(0, baseUrl.length() - 1); // remove trailing slash
        tmdbService = new TmdbService("test-key", baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("CP-35: searchMovies con resultados devuelve lista poblada")
    void searchMovies_withResults_returnsList() {
        String body = """
            {
              "results": [
                {"id": 27205, "title": "Inception", "release_date": "2010-07-15", "poster_path": "/poster.jpg"},
                {"id": 157336, "title": "Interstellar", "release_date": "2014-11-05", "poster_path": "/poster2.jpg"}
              ]
            }
            """;
        mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(body));

        List<TmdbMovieSearchResponse> results = tmdbService.searchMovies("inception");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(27205L);
        assertThat(results.get(0).title()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("CP-36: searchMovies sin resultados devuelve lista vacía")
    void searchMovies_noResults_returnsEmptyList() {
        mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody("{\"results\": []}"));

        List<TmdbMovieSearchResponse> results = tmdbService.searchMovies("zzzzzz");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("CP-37: getMovieDetails mapea correctamente título, director, actores y géneros")
    void getMovieDetails_validId_returnsMappedDto() {
        String movieBody = """
            {
              "id": 27205,
              "title": "Inception",
              "runtime": 148,
              "release_date": "2010-07-15",
              "backdrop_path": "/backdrop.jpg",
              "poster_path": "/poster.jpg",
              "overview": "A thief who steals corporate secrets...",
              "production_countries": [{"iso_3166_1": "US", "name": "United States"}],
              "genres": [{"id": 28, "name": "Action"}, {"id": 18, "name": "Drama"}],
              "production_companies": [{"name": "Warner Bros"}]
            }
            """;
        String creditsBody = """
            {
              "cast": [
                {"id": 1, "name": "Leonardo DiCaprio", "profile_path": "/p1.jpg", "order": 0},
                {"id": 2, "name": "Joseph Gordon-Levitt", "profile_path": "/p2.jpg", "order": 1}
              ],
              "crew": [
                {"id": 525, "name": "Christopher Nolan", "profile_path": "/cn.jpg", "job": "Director"}
              ]
            }
            """;

        mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json").setBody(movieBody));
        mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json").setBody(creditsBody));

        MovieDetailsDTO dto = tmdbService.getMovieDetails(27205L);

        assertThat(dto.title()).isEqualTo("Inception");
        assertThat(dto.director()).isEqualTo("Christopher Nolan");
        assertThat(dto.actors()).contains("Leonardo DiCaprio");
        assertThat(dto.genres()).contains("Action").contains("Drama");
        assertThat(dto.productionCountries()).isEqualTo("United States");
    }
}
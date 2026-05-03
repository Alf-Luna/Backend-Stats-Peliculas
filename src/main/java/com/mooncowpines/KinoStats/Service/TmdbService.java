package com.mooncowpines.KinoStats.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mooncowpines.KinoStats.DTO.TmdbMovieResponse;

@Service
public class TmdbService {
    private final WebClient webClient;

    public TmdbService(@Value("${tmdb.api.key}") String apiKey,
                       @Value("${tmdb.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
    }

    public TmdbMovieResponse fetchMovie(Long tmdbId) {
        return webClient.get()
            .uri("/movie/{id}", tmdbId)
            .retrieve()
            .bodyToMono(TmdbMovieResponse.class)
            .block();
    }
}

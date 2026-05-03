package com.mooncowpines.KinoStats.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieResponse (
    Long id,
    String title,
    Integer runtime,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("production_countries") List<ProductionCountry> productionCountries
){
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductionCountry(
        @JsonProperty("iso_3166_1") String iso,
        String name
    ) {}
}

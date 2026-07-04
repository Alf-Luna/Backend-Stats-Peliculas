package com.mooncowpines.KinoStats.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.DTO.TmdbCreditsResponse;
import com.mooncowpines.KinoStats.DTO.TmdbMovieResponse;
import com.mooncowpines.KinoStats.Model.Country;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.Genre;
import com.mooncowpines.KinoStats.Model.Job;
import com.mooncowpines.KinoStats.Model.MovieRole;
import com.mooncowpines.KinoStats.Model.Person;
import com.mooncowpines.KinoStats.Repository.CountryRepository;
import com.mooncowpines.KinoStats.Repository.FilmRepository;
import com.mooncowpines.KinoStats.Repository.GenreRepository;
import com.mooncowpines.KinoStats.Repository.JobRepository;
import com.mooncowpines.KinoStats.Repository.MovieRoleRepository;
import com.mooncowpines.KinoStats.Repository.PersonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilmService {
    
    private final FilmRepository filmRepository;
    private final TmdbService tmdbService;

    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final JobRepository jobRepository;
    private final PersonRepository personRepository;
    private final MovieRoleRepository movieRoleRepository;

    // public Film getFilmById(Long id){
    //     return filmRepository.findById(id).orElse(null);
    // }

    public Film findOrFetchAndSave(Long id){
        Optional<Film> film = filmRepository.findById(id);

        if (film.isPresent()){
            return film.get();
        }

        Film newFilm = fetchAndSaveFromTmdb(id);
        saveCredits(newFilm);
        return newFilm;
    }

    private Film fetchAndSaveFromTmdb(Long tmdbId) {
        TmdbMovieResponse response = tmdbService.fetchMovie(tmdbId);

        Film film = new Film();
        film.setId(response.id());
        film.setTitle(response.title());
        film.setDateAddedToDB(LocalDate.now());
        film.setPosterPath(response.posterPath());

        if (response.releaseDate() != null && !response.releaseDate().isEmpty()) {
            film.setReleaseYear(LocalDate.parse(response.releaseDate()).getYear());
        }

        if (response.runtime() != null) {
            film.setLengthInMinutes(response.runtime());
        }

        if (response.productionCountries() != null) {
            Set<Country> countries = response.productionCountries().stream()
                    .map(pc -> countryRepository.findById(pc.iso()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            film.setCountries(countries);
        }

        if (response.genres() != null) {
            Set<Genre> genres = response.genres().stream()
                    .map(g -> genreRepository.findById(g.id()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        return filmRepository.save(film);
    }

    public void saveCredits(Film film){
        TmdbCreditsResponse response = tmdbService.fetchMovieCredits(film.getId());

        Job jobActor = jobRepository.findById(2L).get(); //harcoded pero los trabajos planeo insertarlos manualmente asi que esta bbien

        response.cast().stream()
            .limit(9)
            .forEach(member -> {
                Optional<Person> personaOpt = personRepository.findById(member.id());
                Person persona;
                if (!personaOpt.isPresent()){
                    persona = new Person();
                    persona.setId(member.id());
                    persona.setName(member.name());
                    persona.setProfilePath(member.profilePath());
                    personRepository.save(persona);
                } else {
                    persona = personaOpt.get();
                }

                MovieRole newMovieRole = new MovieRole();
                newMovieRole.setFilm(film);
                newMovieRole.setJob(jobActor);
                newMovieRole.setPerson(persona);
                movieRoleRepository.save(newMovieRole);
            });

        Job jobDirector = jobRepository.findById(1L).get();
        response.crew().stream()
            .filter(member -> member.job().equals(jobDirector.getName()))
            .forEach(member -> {
                Optional<Person> personaOpt = personRepository.findById(member.id());
                Person persona;
                if (!personaOpt.isPresent()){
                    persona = new Person();
                    persona.setId(member.id());
                    persona.setName(member.name());
                    persona.setProfilePath(member.profilePath());
                    personRepository.save(persona);
                } else {
                    persona = personaOpt.get();
                }

                MovieRole newMovieRole = new MovieRole();
                newMovieRole.setFilm(film);
                newMovieRole.setJob(jobDirector);
                newMovieRole.setPerson(persona);
                movieRoleRepository.save(newMovieRole);
            });
            
    }
}

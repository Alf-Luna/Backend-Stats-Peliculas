package com.mooncowpines.KinoStats.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.DTO.LogRequestDTO;
import com.mooncowpines.KinoStats.DTO.TmdbMovieResponse;
import com.mooncowpines.KinoStats.Model.Country;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.CountryRepository;
import com.mooncowpines.KinoStats.Repository.FilmRepository;
import com.mooncowpines.KinoStats.Repository.LogRepository;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
    
    private final LogRepository logRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final TmdbService tmdbService;
    private final CountryRepository countryRepository;

    public List<Log> getLogs(){
        return logRepository.findAll();
    }

    public List<Log> getLogsByUserId(Long id){
        return logRepository.findByUserId(id);
    }

    public Optional<Log> getLogById(Long id){
        return logRepository.findById(id);
    }

    public void addLog(LogRequestDTO logRequest){
        User user = userRepository.findById(logRequest.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Film film = filmRepository.findByApiId(logRequest.getFilmId())
            .orElseGet(() -> fetchAndSaveFromTmdb(logRequest.getFilmId()));
        
        Log log = new Log();
        log.setDate(logRequest.getDate());
        log.setReview(logRequest.getReview());
        log.setRating(logRequest.getRating());
        log.setFilm(film);
        log.setUser(user);
        log.setFirstWatch(logRequest.getFirstWatch());
        
        logRepository.save(log);
    }

    private Film fetchAndSaveFromTmdb(Long tmdbId) {
        TmdbMovieResponse response = tmdbService.fetchMovie(tmdbId);

        Film film = new Film();
        film.setApiId(response.id());
        film.setTitle(response.title());
        film.setDateAddedToDB(LocalDate.now());

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

        return filmRepository.save(film);
    }

    public void updateLog(Log log){
        logRepository.save(log);
    }

    public Log updateLog(Long id, LogRequestDTO request) {
        Log log = getLogById(id).orElseThrow();
        log.setDate(request.getDate());
        log.setReview(request.getReview());
        log.setRating(request.getRating());
        log.setFirstWatch(request.getFirstWatch());
        return logRepository.save(log);
    }
}

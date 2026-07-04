package com.mooncowpines.KinoStats.Service;

import com.mooncowpines.KinoStats.DTO.LoginRequest;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.DTO.LogDTO;
import com.mooncowpines.KinoStats.DTO.LogRequestDTO;
import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.LogRepository;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final FilmService filmRepository;
    private final UserRepository userRepository;

    public List<LogDTO> getLogsByUserId(Long id) {
        List<Log> logs = logRepository.findByUserIdOrderByDateDesc(id);
        List<LogDTO> logsDTDtos = logs.stream()
                .map(LogDTO::logToDto)
                .toList();
        return logsDTDtos;
    }

    public Optional<LogDTO> getLogById(Long id) {
        return logRepository.findById(id)
            .map(LogDTO::logToDto);
    }

    public void addLog(LogRequestDTO logRequest) {
        User user = userRepository.findById(logRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Film film = filmRepository.findOrFetchAndSave(logRequest.getFilmId());

        Log log = new Log();
        log.setDate(logRequest.getDate());
        log.setReview(logRequest.getReview());
        if (logRequest.getRating() != null){
            log.setRating(Math.clamp(logRequest.getRating(), 0.5f, 5));
        }
        else{
            log.setRating(null);
        }
        log.setFilm(film);
        log.setUser(user);
        log.setFirstWatch(logRequest.getFirstWatch());

        logRepository.save(log);
    }

    public void updateLog(Log log) {
        logRepository.save(log);
    }

    public Log updateLog(Long id, LogRequestDTO request) {
        Log log = logRepository.findById(id).orElseThrow();
        log.setDate(request.getDate());
        log.setReview(request.getReview());
        if (request.getRating() != null){
            log.setRating(Math.clamp(request.getRating(), 0.5f, 5));
        }
        else{
            log.setRating(null);
        }
        log.setFirstWatch(request.getFirstWatch());
        return logRepository.save(log);
    }

    public void deleteLog(Long id){
        logRepository.deleteById(id);
    }

    public List<Log> findLatestLogs(Long id){
        return logRepository.findTop6ByUserIdOrderByDateDesc(id);
    }
}

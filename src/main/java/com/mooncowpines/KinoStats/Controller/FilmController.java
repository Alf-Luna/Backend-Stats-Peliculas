package com.mooncowpines.KinoStats.Controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Service.FilmService;

@RestController
@RequestMapping("api/v1/films")
public class FilmController {
    
    @Autowired
    private FilmService filmService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable Long id){
        Optional<Film> film = filmService.getFilmById(id);

        if (film.isPresent()) {
            return ResponseEntity.ok()
                        .header("Header", "Values")
                        .body(film.get());
        }
        else {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "Registro no encontrado");
            errorBody.put("status", "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFilm(@RequestBody Film film){
        film.setDateAddedToDB(LocalDate.now());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(film.getId())
            .toUri();

        return ResponseEntity.created(location).body(film);
    }
}

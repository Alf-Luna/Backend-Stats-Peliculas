package com.mooncowpines.KinoStats.Controller;

import java.net.URI;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
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
        Film film = filmService.getFilmById(id);
        return (film != null) ? ResponseEntity.ok(film) : ResponseEntity.notFound().build();
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

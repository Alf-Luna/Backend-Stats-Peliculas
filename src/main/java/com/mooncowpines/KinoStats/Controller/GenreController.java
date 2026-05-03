package com.mooncowpines.KinoStats.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mooncowpines.KinoStats.Model.Genre;
import com.mooncowpines.KinoStats.Service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    @Autowired
    private GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        return service.findAll();
    }

    @PostMapping
    public Genre create(@RequestBody Genre genre) {
        return service.save(genre);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getById(@PathVariable Long id) {
        Genre genre = service.findById(id);
        return (genre != null) ? ResponseEntity.ok(genre) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
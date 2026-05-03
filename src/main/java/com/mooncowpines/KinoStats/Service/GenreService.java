package com.mooncowpines.KinoStats.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.Model.Genre;
import com.mooncowpines.KinoStats.Repository.GenreRepository;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        genreRepository.deleteById(id);
    }
}
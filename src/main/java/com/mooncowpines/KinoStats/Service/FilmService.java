package com.mooncowpines.KinoStats.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.Model.Film;
import com.mooncowpines.KinoStats.Repository.FilmRepository;

@Service
public class FilmService {
    
    @Autowired
    FilmRepository filmRepository;

    public List<Film> getFilms(){
        return filmRepository.findAll();
    }

    public Optional<Film> getFilmById(Long id){
        return filmRepository.findById(id);
    }
}

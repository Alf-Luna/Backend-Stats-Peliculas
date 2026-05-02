package com.mooncowpines.KinoStats.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mooncowpines.KinoStats.Model.Film;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    
}

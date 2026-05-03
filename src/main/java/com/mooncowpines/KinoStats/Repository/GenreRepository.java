package com.mooncowpines.KinoStats.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mooncowpines.KinoStats.Model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
}

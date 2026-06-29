package com.mooncowpines.KinoStats.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Repository.Projections.DecadeWatches;
import com.mooncowpines.KinoStats.Repository.Projections.RatingsCount;
import com.mooncowpines.KinoStats.Repository.Projections.TypeWatches;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByUserId(Long userId);
    List<Log> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT COUNT(l) FROM Log l WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate")
    Integer countMoviesWatched(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(f.lengthInMinutes) FROM Log l JOIN l.film f WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate")
    Integer sumWatchTime(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT g.name as name, COUNT(l) as watches FROM Log l " +
            "JOIN l.film f JOIN f.genres g " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY g.name ORDER BY watches DESC")
    List<TypeWatches> watchesByGenre(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT c.name AS name, COUNT(l) AS watches FROM Log l " +
            "JOIN l.film f JOIN f.countries c " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY c.name ORDER BY watches DESC")
    List<TypeWatches> watchesByCountry(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p.name AS name, COUNT(l) AS watches FROM Log l " +
            "JOIN l.film f " +
            "JOIN MovieRole mr ON mr.film = f " +
            "JOIN mr.person p " +
            "WHERE l.user.id = :userId " +
            "AND mr.job.id = :jobId " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name ORDER BY watches DESC")
    List<TypeWatches> watchesByPerson(Long userId, Long jobId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT rating as rating, COUNT(l) as amount FROM Log l " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY rating")
    List<RatingsCount> countsByRating(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT (f.releaseYear / 10) * 10 AS decades, COUNT(l) AS watches FROM Log l " +
            "JOIN l.film f " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY (f.releaseYear / 10) * 10 ORDER BY decades")
    List<DecadeWatches> watchesByDecade(Long userId, LocalDate startDate, LocalDate endDate);

    List<Log> findTop6ByUserIdOrderByDateDesc(Long userId);
    Optional<Log> findFirstByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
    Optional<Log> findFirstByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
}

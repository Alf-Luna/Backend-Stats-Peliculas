package com.mooncowpines.KinoStats.Model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "film")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long apiId;

    @Column(nullable = false)
    private String title;

    private Integer releaseYear;

    private String director;

    private String cinematographer;

    private int lengthInMinutes;

    private String countryOfOrigin;

    private LocalDate dateAddedToDB;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "film_genres",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "film_countries",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "country_code")
    )
    private Set<Country> countries = new HashSet<>();
}

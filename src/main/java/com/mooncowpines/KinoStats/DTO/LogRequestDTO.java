package com.mooncowpines.KinoStats.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogRequestDTO {
    private LocalDate date;
    private String review;
    private Float rating;
    private Long filmId;
    private Long userId;
    private Boolean firstWatch;
}

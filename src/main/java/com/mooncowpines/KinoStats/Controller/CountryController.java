package com.mooncowpines.KinoStats.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mooncowpines.KinoStats.Model.Country;
import com.mooncowpines.KinoStats.Service.CountryService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/all")
    public List<Country> getAll() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        Optional<Country> film = countryService.getCountryByCode(code);

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
    public ResponseEntity<?> addCountry(@RequestBody Country country) {
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(country.getCode())
                        .toUri();

        return ResponseEntity.created(location).body(country);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        countryService.deleteCountry(code);
        return ResponseEntity.noContent().build();
    }
}
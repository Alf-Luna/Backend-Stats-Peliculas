package com.mooncowpines.KinoStats.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mooncowpines.KinoStats.Model.Country;
import com.mooncowpines.KinoStats.Service.CountryService;

import java.net.URI;
import java.util.List;

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
        Country country = countryService.getCountryByCode(code);
        return (country != null) ? ResponseEntity.ok(country) : ResponseEntity.notFound().build();
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
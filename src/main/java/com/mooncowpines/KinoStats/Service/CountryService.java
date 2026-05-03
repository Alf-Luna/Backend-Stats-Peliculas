package com.mooncowpines.KinoStats.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.Model.Country;
import com.mooncowpines.KinoStats.Repository.CountryRepository;

import java.util.List;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getCountryByCode(String code) {
        return countryRepository.findById(code).orElse(null);
    }

    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    public void deleteCountry(String code) {
        countryRepository.deleteById(code);
    }
}
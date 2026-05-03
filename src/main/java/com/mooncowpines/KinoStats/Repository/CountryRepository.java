package com.mooncowpines.KinoStats.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mooncowpines.KinoStats.Model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

}
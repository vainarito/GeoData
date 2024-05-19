package com.example.geodata.repository;

import com.example.geodata.entity.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query(value = "SELECT c.id, c.country_name, c.nationality,"
            + " c.latitude, c.longitude FROM countries c "
            + "JOIN countries_languages cl ON c.id = cl.country_id "
            + "JOIN languages l ON cl.language_id = l.id "
            + "WHERE l.language_name = (?1)", nativeQuery = true)
    List<Country> findAllCountriesContainingSpecifiedLanguage(
            @Param("1") String name
    );

    Optional<Country> findCountryByName(String name);

    Boolean existsByName(String name);

}

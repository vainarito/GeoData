package com.example.geodata.service;

import com.example.geodata.dto.CountryDTO;
import com.example.geodata.entity.Country;
import com.example.geodata.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface CountryService {

    List<Country> getAll();

    Optional<Country> findById(Integer id)
            throws ResourceNotFoundException;

    Country createCountry(CountryDTO countryDTO);

    void deleteById(Integer id)
            throws ResourceNotFoundException;

    Country deleteLanguage(CountryDTO countryDTO)
            throws ResourceNotFoundException;

    Country addLanguage(CountryDTO countryDTO)
            throws ResourceNotFoundException;

    Country updateInfo(CountryDTO countryDTO)
            throws ResourceNotFoundException;

    List<Country> findCountriesWithSpecifiedLanguage(String name) throws ResourceNotFoundException;

    void bulkInsert(List<CountryDTO> countryDTOS);

}

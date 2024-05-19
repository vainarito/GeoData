package com.example.geodata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.geodata.controller.CountryController;
import com.example.geodata.dto.CountryDTO;
import com.example.geodata.entity.Country;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.service.CountryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    @Test
    void getAll() {
        ResponseEntity<List<Country>> responseEntity = countryController
                .getAll();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void findById()
            throws ResourceNotFoundException {
        int id = 1;
        when(countryService.findById(id))
                .thenReturn(Optional.ofNullable(any(Country.class)));

        ResponseEntity<Optional<Country>> responseEntity = countryController
                .findById(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void addCountry() {
        ResponseEntity<Country> responseEntity = countryController
                .addCountry(any(CountryDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteCountryById()
            throws ResourceNotFoundException {
        int id = 1;

        HttpStatus httpStatus = countryController
                .deleteCountryById(id);

        assertEquals(HttpStatus.OK, httpStatus);
    }

    @Test
    void updateInfo()
            throws ResourceNotFoundException {
        countryController.updateInfo(any(CountryDTO.class));

        ResponseEntity<Country> responseEntity = countryController
                .updateInfo(any(CountryDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void addLanguages()
            throws ResourceNotFoundException {
        countryController.addLanguages(any(CountryDTO.class));

        ResponseEntity<Country> responseEntity = countryController
                .addLanguages(any(CountryDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteLanguages()
            throws ResourceNotFoundException {
        countryController.deleteLanguages(any(CountryDTO.class));

        ResponseEntity<Country> responseEntity = countryController
                .deleteLanguages(any(CountryDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void bulkInsert() {
        HttpStatus httpStatus = countryController
                .bulkInsert(new ArrayList<>());

        assertEquals(HttpStatus.OK, httpStatus);
    }

}
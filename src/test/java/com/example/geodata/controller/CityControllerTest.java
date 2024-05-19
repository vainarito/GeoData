package com.example.geodata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.geodata.controller.CityController;
import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.service.CityService;
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
class CityControllerTest {

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;


    @Test
    void getAll() {
        ResponseEntity<List<City>> responseEntity = cityController
                .getAll();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void findById()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<City> mockCity = Optional.of(new City());

        when(cityService.findById(id))
                .thenReturn(mockCity);
        ResponseEntity<Optional<City>> responseEntity = cityController
                .findById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void addCity()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .name("Minsk")
                .countryName("Belarus")
                .latitude(22.4567)
                .longitude(23.2134)
                .build();
        City createdCity = new City();

        when(cityService.createCity(cityDTO))
                .thenReturn(createdCity);

        ResponseEntity<City> responseEntity = cityController
                .addCity(cityDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteCityById()
            throws ResourceNotFoundException {
        int id = 1;

        HttpStatus httpStatus = cityController.deleteCityById(id);

        assertEquals(HttpStatus.OK, httpStatus);
    }

    @Test
    void changeCountry()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .id(2)
                .name("Minsk")
                .countryName("Belarus")
                .build();

        ResponseEntity<City> responseEntity = cityController
                .changeCountry(cityDTO);

        assertEquals(HttpStatus.OK,
                responseEntity.getStatusCode());
    }

    @Test
    void updateInfo()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .id(1)
                .latitude(23.4444)
                .build();

        ResponseEntity<City> responseEntity = cityController
                .updateInfo(cityDTO);

        assertEquals(HttpStatus.OK,
                responseEntity.getStatusCode());
    }

    @Test
    void bulkInsert() {
        HttpStatus httpStatus = cityController
                .bulkInsert(new ArrayList<>());

        assertEquals(HttpStatus.OK, httpStatus);
    }

}
package com.example.geodata.service;

import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface CityService {

    List<City> getAll();

    void deleteById(Integer id)
            throws ResourceNotFoundException;

    Optional<City> findById(Integer id)
            throws ResourceNotFoundException;

    City createCity(CityDTO cityDTO)
            throws ResourceNotFoundException;

    City replaceCountry(CityDTO cityDTO)
            throws ResourceNotFoundException;

    City update(CityDTO cityDTO)
            throws ResourceNotFoundException;

    void bulkInsert(List<CityDTO> cityDTOS);

}

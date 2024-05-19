package com.example.geodata.controller;

import com.example.geodata.aspects.AspectAnnotation;
import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.service.CityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@Tag(name = "CityController")
@RestController
@RequestMapping("/api/cities")
@AllArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("/all")
    public ResponseEntity<List<City>> getAll() {
        return ResponseEntity.ok(cityService.getAll());
    }

    @GetMapping("/info/{cityId}")
    @AspectAnnotation
    public ResponseEntity<Optional<City>> findById(
            @PathVariable final Integer cityId
    )
            throws ResourceNotFoundException {
        return ResponseEntity.ok(cityService.findById(cityId));
    }

    @PostMapping("/create")
    @AspectAnnotation
    public ResponseEntity<City> addCity(@RequestBody final CityDTO cityDTO)
            throws ResourceNotFoundException {
        return new ResponseEntity<>(cityService.createCity(cityDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{cityId}")
    @AspectAnnotation
    public HttpStatus deleteCityById(@PathVariable final Integer cityId)
            throws ResourceNotFoundException {
        cityService.deleteById(cityId);
        return HttpStatus.OK;
    }

    @PutMapping("/changeCountry")
    @AspectAnnotation
    public ResponseEntity<City> changeCountry(
            @RequestBody final CityDTO cityDTO
    )
            throws ResourceNotFoundException {
        City city = cityService.replaceCountry(cityDTO);
        return new ResponseEntity<>(city, HttpStatus.OK);
    }

    @PutMapping("/updateInfo")
    @AspectAnnotation
    public ResponseEntity<City> updateInfo(@RequestBody final CityDTO cityDTO)
            throws ResourceNotFoundException {
        City city = cityService.update(cityDTO);
        return new ResponseEntity<>(city, HttpStatus.OK);
    }

    @PostMapping("/bulkInsert")
    @AspectAnnotation
    public HttpStatus bulkInsert(@RequestBody final List<CityDTO> cityDTOS) {
        cityService.bulkInsert(cityDTOS);
        return HttpStatus.OK;
    }

}

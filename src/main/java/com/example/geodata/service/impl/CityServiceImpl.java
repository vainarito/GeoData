package com.example.geodata.service.impl;

import com.example.geodata.aspects.AspectAnnotation;
import com.example.geodata.cache.LRUCacheCity;
import com.example.geodata.cache.LRUCacheCountry;
import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.entity.Country;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.CityRepository;
import com.example.geodata.repository.CountryRepository;
import com.example.geodata.service.CityService;
import com.example.geodata.service.utility.CityDTOUtility;
import io.micrometer.common.lang.NonNullApi;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@NonNullApi
@Service
@AllArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final JdbcTemplate jdbcTemplate;
    private final LRUCacheCity cityCache;
    private final LRUCacheCountry countryCache;
    private static final String NO_EXIST = "City don't exist with id =";

    @Override
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    @Override
    @AspectAnnotation
    public void deleteById(final Integer id)
            throws ResourceNotFoundException {
        Optional<City> city = cityRepository.findById(id);
        if (city.isPresent()) {
            cityCache.remove(id);
            countryCache.remove(city.get().getCountry().getId());
            cityRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(NO_EXIST + " " + id);
        }
    }

    @Override
    @AspectAnnotation
    public Optional<City> findById(final Integer id)
            throws ResourceNotFoundException {
        Optional<City> city = cityCache.get(id);
        if (city.isEmpty()) {
            city = cityRepository.findById(id);
            if (city.isEmpty()) {
                throw new ResourceNotFoundException(NO_EXIST + " " + id);
            }
            cityCache.put(id, city.get());
        }
        return city;
    }

    @Override
    @AspectAnnotation
    public City createCity(final CityDTO cityDTO)
            throws ResourceNotFoundException {
        Optional<Country> country = countryRepository
                .findCountryByName(cityDTO.countryName());
        if (country.isPresent()) {
            if (cityDTO.name() == null || cityDTO.longitude() == null
                    || cityDTO.latitude() == null) {
                throw new BadRequestException("All fields: "
                        + "[name, latitude, longitude]"
                        + "must be provided.");
            }
            City city = CityDTOUtility.buildCityFromDTO(cityDTO, country.get());
            cityRepository.save(city);
            cityCache.put(city.getId(), city);
            countryCache.remove(city.getCountry().getId());
            return city;
        } else {
            throw new ResourceNotFoundException("Country with name :: "
                    + cityDTO.countryName() + " not found.");
        }
    }

    @Override
    @AspectAnnotation
    public City replaceCountry(final CityDTO cityDTO)
            throws ResourceNotFoundException {
        Optional<City> city = cityRepository.findById(cityDTO.id());
        if (city.isPresent()) {
            Optional<Country> country = countryRepository
                    .findCountryByName(cityDTO.countryName());
            if (country.isPresent()) {
                city.get().setCountry(country.get());
                cityRepository.save(city.get());
                cityCache.put(city.get().getId(), city.get());
                countryCache.remove(city.get().getCountry().getId());
                return city.get();
            } else {
                throw new ResourceNotFoundException("Country with name :: "
                        + cityDTO.countryName() + " not found.");
            }
        } else {
            throw new ResourceNotFoundException(NO_EXIST + " " + cityDTO.id());
        }
    }

    @Override
    @AspectAnnotation
    public City update(final CityDTO cityDTO) throws ResourceNotFoundException {
        Optional<City> city = cityRepository.findById(cityDTO.id());
        if (city.isPresent()) {
            if (cityDTO.latitude() != null) {
                city.get().setLatitude(cityDTO.latitude());
            }
            if (cityDTO.longitude() != null) {
                city.get().setLongitude(cityDTO.longitude());
            }
            if (cityDTO.name() != null) {
                city.get().setName(cityDTO.name());
            }
            countryCache.remove(city.get().getCountry().getId());
            cityCache.put(city.get().getId(), city.get());
            cityRepository.save(city.get());
            return city.get();
        } else {
            throw new ResourceNotFoundException(NO_EXIST + " " + cityDTO.id());
        }
    }

    @Transactional
    @Override
    public void bulkInsert(List<CityDTO> cityDTOS) {
        List<City> cities = cityDTOS.stream()
                .map(cityDTO -> {
                    Optional<Country> country = countryRepository
                            .findCountryByName(cityDTO.countryName());
                    return country.map(countryObj -> CityDTOUtility.
                            buildCityFromDTO(cityDTO, countryObj));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        jdbcTemplate.batchUpdate("INSERT into cities"
                + " (city_name, fk_cities_countries, latitude, longitude)"
                + " VALUES (?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i)
                    throws SQLException {
                preparedStatement.setString(1, cities.get(i).getName());
                preparedStatement.setInt(2, cities.get(i).getCountry().getId());
                preparedStatement.setDouble(3, cities.get(i).getLatitude());
                preparedStatement.setDouble(4, cities.get(i).getLongitude());
            }

            @Override
            public int getBatchSize() {
                return cities.size();
            }
        });
    }

}

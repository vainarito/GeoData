package com.example.geodata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.geodata.cache.LRUCacheCity;
import com.example.geodata.cache.LRUCacheCountry;
import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.entity.Country;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.CityRepository;
import com.example.geodata.repository.CountryRepository;
import com.example.geodata.service.impl.CityServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private LRUCacheCity cityCache;

    @Mock
    private LRUCacheCountry cacheCountry;

    @InjectMocks
    private CityServiceImpl cityService;

    @Test
    void getAllCities() {
        List<City> expectedCities = new ArrayList<>();

        when(cityRepository.findAll())
                .thenReturn(expectedCities);

        List<City> actualCities = cityService.getAll();

        assertEquals(expectedCities, actualCities);
    }

    @Test
    void deleteCityById_existingId()
            throws ResourceNotFoundException {
        int cityId = 1;
        Optional<City> expectedCity = Optional.of(City.builder()
                .id(cityId)
                .country(Country.builder()
                        .id(2)
                        .build())
                .build());
        when(cityRepository.findById(cityId)).thenReturn(expectedCity);

        cityService.deleteById(cityId);
        verify(cityRepository, times(1)).deleteById(cityId);
    }

    @Test
    void deleteCityById_invalidId() {
        int id = 1;
        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cityService.deleteById(id));
    }

    @Test
    void findCityById_existingIdAndCityNotInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<City> expectedCity = Optional.of(new City());

        when(cityCache.get(id)).thenReturn(Optional.empty());
        when(cityRepository.findById(id)).thenReturn(expectedCity);

        Optional<City> actualCity = cityService.findById(id);
        assertEquals(expectedCity, actualCity);
        verify(cityCache, times(1))
                .put(id, expectedCity.get());
    }

    @Test
    void findCityById_existingIdAndCityInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<City> expectedCity = Optional.of(new City());

        when(cityCache.get(id)).thenReturn(expectedCity);

        Optional<City> actualCity = cityService.findById(id);

        assertEquals(expectedCity, actualCity);
        verify(cityRepository, never()).findById(anyInt());
    }

    @Test
    void findCityById_invalidId() {
        int id = 1;

        when(cityCache.get(id)).thenReturn(Optional.empty());
        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cityService.findById(id));
    }

    @Test
    void createCity_invalidCountryName() {
        CityDTO cityDTO = CityDTO.builder()
                .countryName("Belarus")
                .build();

        when(countryRepository.findCountryByName(cityDTO.countryName()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cityService.createCity(cityDTO));
    }

    @Test
    void createCity_illegalArguments() {
        CityDTO cityDTO = CityDTO.builder()
                .countryName("Belarus")
                .build();
        Optional<Country> country = Optional.of(new Country());

        when(countryRepository.findCountryByName(cityDTO.countryName()))
                .thenReturn(country);

        assertThrows(BadRequestException.class,
                () -> cityService.createCity(cityDTO));
    }

    @Test
    void createCity_Success()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .name("Tokyo")
                .countryName("Japan")
                .latitude(13.4543)
                .longitude(12.4445)
                .build();
        Optional<Country> country = Optional.of(Country.builder()
                .id(1)
                .name("Japan")
                .build());

        when(countryRepository.findCountryByName(cityDTO.countryName()))
                .thenReturn(country);

        City createdCity = cityService.createCity(cityDTO);

        assertEquals(createdCity.getName(), cityDTO.name());
        assertEquals(createdCity.getLatitude(), cityDTO.latitude());
        assertEquals(createdCity.getLongitude(), cityDTO.longitude());
        assertEquals(createdCity.getCountry().getName(), cityDTO.countryName());
    }

    @Test
    void replaceCountry_invalidCityId() {
        CityDTO cityDTO = CityDTO.builder()
                .build();
        when(cityRepository.findById(cityDTO.id())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cityService.replaceCountry(cityDTO));
    }

    @Test
    void replaceCountry_invalidCountryName() {
        CityDTO cityDTO = CityDTO.builder()
                .id(1)
                .build();
        Optional<City> city = Optional.of(new City());

        when(cityRepository.findById(cityDTO.id()))
                .thenReturn(city);
        when(countryRepository.findCountryByName(cityDTO.countryName()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cityService.replaceCountry(cityDTO));
    }

    @Test
    void replaceCountry_Success()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .id(1)
                .countryName("Belarus")
                .build();
        Optional<City> city = Optional.of(new City());
        Optional<Country> country = Optional.of(Country.builder()
                .id(2)
                .name("Belarus")
                .build());

        when(cityRepository.findById(cityDTO.id()))
                .thenReturn(city);
        when(countryRepository.findCountryByName(cityDTO.countryName()))
                .thenReturn(country);

        City updatedCity =  cityService
                .replaceCountry(cityDTO);

        assertEquals(updatedCity.getCountry().getName(), cityDTO.countryName());

    }

    @Test
    void updateCity_validId()
            throws ResourceNotFoundException {
        CityDTO cityDTO = CityDTO.builder()
                .id(1)
                .name("Minsk")
                .latitude(22.4234)
                .longitude(12.5612)
                .build();
        Optional<City> expectedCity = Optional.of(City.builder()
                .country(Country.builder()
                        .id(2)
                        .build())
                .build());

        when(cityRepository.findById(cityDTO.id())).thenReturn(expectedCity);

        City updatedCity = cityService.update(cityDTO);

        assertEquals(updatedCity.getName(), cityDTO.name());
        assertEquals(updatedCity.getLatitude(), cityDTO.latitude());
        assertEquals(updatedCity.getLongitude(), cityDTO.longitude());
        verify(cacheCountry, times(1)).remove(anyInt());
    }

    @Test
    void updateCity_invalidId() {
        CityDTO cityDTO = CityDTO.builder()
                .id(1)
                .build();

        when(cityRepository.findById(cityDTO.id())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cityService.update(cityDTO));
    }

    @Test
    void bulkInsert() {
        CityDTO firstCity = CityDTO.builder()
                .name("Minsk")
                .longitude(22.2521)
                .latitude(32.4544)
                .countryName("Belarus")
                .build();
        CityDTO secondCity = CityDTO.builder()
                .name("Pinsk")
                .longitude(17.3630)
                .latitude(44.2135)
                .countryName("Belarus")
                .build();

        List<CityDTO> cityDTOS = Arrays
                .asList(firstCity, secondCity);

        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findCountryByName(anyString()))
                .thenReturn(expectedCountry);

        cityService.bulkInsert(cityDTOS);

        verify(jdbcTemplate, times(1))
                .batchUpdate(eq("INSERT into cities"
                        + " (city_name, fk_cities_countries, latitude, longitude)"
                        + " VALUES (?, ?, ?, ?)"), any(BatchPreparedStatementSetter.class));

    }
}

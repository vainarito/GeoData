package com.example.geodata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.geodata.cache.LRUCacheCountry;
import com.example.geodata.dto.CountryDTO;
import com.example.geodata.entity.Country;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.CountryRepository;
import com.example.geodata.repository.LanguageRepository;
import com.example.geodata.service.impl.CountryServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private LRUCacheCountry countryCache;

    @InjectMocks
    private CountryServiceImpl countryService;

    @Test
    void getAllCountries() {
        List<Country> expectedCountries = new ArrayList<>();

        when(countryRepository.findAll())
                .thenReturn(expectedCountries);

        List<Country> actualCountries = countryService.getAll();

        assertEquals(expectedCountries, actualCountries);
    }

    @Test
    void findCountryById_invalidId() {
        int id = 1;

        when(countryCache.get(id)).thenReturn(Optional.empty());
        when(countryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.findById(id));
    }

    @Test
    void findCountryById_existingIdAndCountryNotInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryCache.get(id)).thenReturn(Optional.empty());
        when(countryRepository.findById(id)).thenReturn(expectedCountry);

        Optional<Country> actualCountry = countryService.findById(id);
        assertEquals(expectedCountry, actualCountry);
        verify(countryCache, times(1))
                .put(id, expectedCountry.get());
    }

    @Test
    void findCountryById_existingIdAndCountryInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryCache.get(id)).thenReturn(expectedCountry);

        Optional<Country> actualCountry = countryService.findById(id);

        assertEquals(expectedCountry, actualCountry);
        verify(countryRepository, never()).findById(anyInt());
    }

    @Test
    void createCountry_existingCountry() {
        CountryDTO countryDTO = CountryDTO.builder()
                .name("Japan")
                .build();

        when(countryRepository.existsByName(countryDTO.name()))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> countryService.createCountry(countryDTO));
    }

    @Test
    void createCountry_illegalArguments() {
        CountryDTO countryDTO = CountryDTO.builder()
                .name("Japan")
                .build();

        when(countryRepository.existsByName(countryDTO.name()))
                .thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> countryService.createCountry(countryDTO));
    }

    @Test
    void createCountry_success() {
        CountryDTO countryDTO = CountryDTO.builder()
                .name("Japan")
                .latitude(13.5677)
                .longitude(15.3322)
                .nationality("Japanese")
                .build();

        when(countryRepository.existsByName(countryDTO.name()))
                .thenReturn(false);

        Country createdCountry = countryService
                .createCountry(countryDTO);
        assertEquals(createdCountry.getName(), countryDTO.name());
        assertEquals(createdCountry.getNationality(), countryDTO.nationality());
        assertEquals(createdCountry.getLatitude(), countryDTO.latitude());
        assertEquals(createdCountry.getLongitude(), countryDTO.longitude());
        verify(countryRepository, times(1))
                .save(any(Country.class));
    }

    @Test
    void deleteCountryById_invalidId() {
        int id = 1;

        when(countryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.deleteById(id));
    }

    @Test
    void deleteCountryById_existingId()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findById(id))
                .thenReturn(expectedCountry);

        countryService.deleteById(id);

        verify(countryRepository, times(1))
                .deleteById(anyInt());
    }

    @Test
    void deleteLanguage_invalidId() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.deleteLanguage(countryDTO));
    }

    @Test
    void deleteLanguage_illegalArguments() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);

        when(languageRepository.findByNames(countryDTO.languages()))
                .thenReturn(new ArrayList<>());

        assertThrows(BadRequestException.class,
                () -> countryService.deleteLanguage(countryDTO));
    }

    @Test
    void deleteLanguage_success()
            throws ResourceNotFoundException {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();
        Language testLanguage = Language.builder()
                .id(1)
                .name("Japanese")
                .code("JPN")
                .build();
        List<Language> expectedLanguages = new ArrayList<>();
        expectedLanguages.add(testLanguage);
        Set<Language> countryLanguages = new HashSet<>();
        countryLanguages.add(testLanguage);
        Optional<Country> expectedCountry = Optional.of(new Country());
        expectedCountry.get().setLanguages(countryLanguages);

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);
        when(languageRepository.findByNames(countryDTO.languages()))
                .thenReturn(expectedLanguages);

        Country actualCountry = countryService
                .deleteLanguage(countryDTO);

        assertTrue(actualCountry.getLanguages().isEmpty());
    }

    @Test
    void addLanguage_invalidId() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.addLanguage(countryDTO));
    }

    @Test
    void addLanguage_illegalArguments() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);
        when(languageRepository.findByNames(countryDTO.languages()))
                .thenReturn(new ArrayList<>());

        assertThrows(BadRequestException.class,
                () -> countryService.addLanguage(countryDTO));
    }

    @Test
    void addLanguage_success()
            throws ResourceNotFoundException {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();
        Optional<Country> expectedCountry = Optional.of(new Country());
        Language language = Language.builder()
                .id(1)
                .name("Russian")
                .code("RUS")
                .build();
        List<Language> languages = new ArrayList<>();
        languages.add(language);

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);
        when(languageRepository.findByNames(countryDTO.languages()))
                .thenReturn(languages);

        Country actualCountry = countryService
                .addLanguage(countryDTO);

        assertFalse(actualCountry.getLanguages().isEmpty());

    }

    @Test
    void updateInfo_invalidId() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .build();

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.updateInfo(countryDTO));
    }

    @Test
    void updateInfo_existingId()
            throws ResourceNotFoundException {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .name("Japan")
                .nationality("Japanese")
                .latitude(43.1234)
                .longitude(12.2323)
                .build();
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);
        when(countryRepository.existsByName(countryDTO.name()))
                .thenReturn(false);

        Country updatedCountry = countryService
                .updateInfo(countryDTO);

        assertEquals(updatedCountry.getName(), countryDTO.name());
        assertEquals(updatedCountry.getNationality(), countryDTO.nationality());
        assertEquals(updatedCountry.getLatitude(), countryDTO.latitude());
        assertEquals(updatedCountry.getLongitude(), countryDTO.longitude());
        verify(countryRepository, times(1))
                .save(any(Country.class));
    }

    @Test
    void updateCountry_replaceForExistingName() {
        CountryDTO countryDTO = CountryDTO.builder()
                .id(1)
                .name("Japan")
                .build();
        Optional<Country> expectedCountry = Optional.of(new Country());

        when(countryRepository.findById(countryDTO.id()))
                .thenReturn(expectedCountry);
        when(countryRepository.existsByName(countryDTO.name()))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> countryService.updateInfo(countryDTO));
    }

    @Test
    void bulkInsert() {
        CountryDTO firstCountry = CountryDTO.builder()
                .name("Belarus")
                .nationality("Belarusian")
                .latitude(12.2420)
                .longitude(23.2412)
                .build();
        CountryDTO secondCountry = CountryDTO.builder()
                .name("Russia")
                .nationality("Russian")
                .latitude(15.3400)
                .longitude(17.2345)
                .build();
        List<CountryDTO> countryDTOS = Arrays
                .asList(firstCountry, secondCountry);

        countryService.bulkInsert(countryDTOS);

        verify(jdbcTemplate, times(1))
                .batchUpdate(eq("INSERT into countries"
                        + " (country_name, nationality, latitude, longitude)"
                        + " VALUES (?, ?, ?, ?)"),
                        any(BatchPreparedStatementSetter.class));
    }

    @Test
    void findCountriesWithSpecLanguage_invalidNameLanguage() {
        String expectedLanguage = "Russian";
        when(languageRepository.existsByName(expectedLanguage))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> countryService
                        .findCountriesWithSpecifiedLanguage(expectedLanguage));
    }

    @Test
    void findCountriesWithSpecLanguage_success()
            throws ResourceNotFoundException {
        String name = "Russian";
        List<Country> expectedCountries = new ArrayList<>();

        when(languageRepository.existsByName(name))
                .thenReturn(true);
        when(countryRepository
                .findAllCountriesContainingSpecifiedLanguage(name))
                .thenReturn(expectedCountries);

        List<Country> actualCountries = countryService
                .findCountriesWithSpecifiedLanguage(name);

        assertEquals(expectedCountries, actualCountries);
    }

}
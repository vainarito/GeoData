package com.example.geodata.service.impl;

import com.example.geodata.aspects.AspectAnnotation;
import com.example.geodata.cache.LRUCacheCity;
import com.example.geodata.cache.LRUCacheCountry;
import com.example.geodata.dto.CountryDTO;
import com.example.geodata.entity.City;
import com.example.geodata.entity.Country;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.CountryRepository;
import com.example.geodata.repository.LanguageRepository;
import com.example.geodata.service.CountryService;
import com.example.geodata.service.utility.CountryDTOUtility;
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
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;
    private final LRUCacheCity cityCache;
    private final JdbcTemplate jdbcTemplate;
    private final LRUCacheCountry countryCache;
    private static final String NO_EXIST = "Country don't exist with id =";
    private static final String ALREADY_EXIST = "Country already exist with name =";

    @Override
    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    @Override
    @AspectAnnotation
    public Optional<Country> findById(final Integer id)
            throws ResourceNotFoundException {
        //Optional<Country> country = countryCache.get(id);
        return countryRepository.findById(id);
    }

    @Override
    @AspectAnnotation
    public Country createCountry(final CountryDTO countryDTO) {
        if (Boolean.TRUE.equals(countryRepository
                .existsByName(countryDTO.name()))) {
            throw new BadRequestException(ALREADY_EXIST
                    + " " + countryDTO.name());
        }
        if (countryDTO.name() == null || countryDTO.longitude() == null
                || countryDTO.latitude() == null
                || countryDTO.nationality() == null) {
            throw new BadRequestException("All fields: "
                    + "[name, nationality, latitude, longitude]"
                    + "must be provided.");
        }
        Country country = CountryDTOUtility
                .buildCountryFromCountryDTO(countryDTO);
        countryRepository.save(country);
        countryCache.put(country.getId(), country);
        return country;
    }

    @Override
    @AspectAnnotation
    public void deleteById(final Integer id)
            throws ResourceNotFoundException {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isPresent()) {
            countryCache.remove(id);
            for (City city : country.get().getCities()) {
                cityCache.remove(city.getId());
            }
            countryRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(NO_EXIST + " " + id);
        }
    }

    @Override
    @AspectAnnotation
        public Country addLanguage(final CountryDTO countryDTO)
            throws ResourceNotFoundException {
            Optional<Country> country =
                    countryRepository.findById(countryDTO.id());
            if (country.isPresent()) {
                List<Language> languageExist = languageRepository
                        .findByNames(countryDTO.languages());
                if (languageExist.isEmpty()) {
                    throw new BadRequestException("List of languages"
                            + " for adding is empty"
                            + " or these languages don't exist.");
                }
                for (Language language : languageExist) {
                    country.get().addLanguage(language);
                }
                countryRepository.save(country.get());
                countryCache.put(country.get().getId(), country.get());
                return country.get();
            }
            throw new ResourceNotFoundException(NO_EXIST
                    + " " + countryDTO.id());
    }

    @Override
    @AspectAnnotation
    public Country deleteLanguage(final CountryDTO countryDTO)
            throws ResourceNotFoundException {
        Optional<Country> country = countryRepository
                .findById(countryDTO.id());
        if (country.isPresent()) {
            List<Language> languages = languageRepository
                    .findByNames(countryDTO.languages());
            if (languages.isEmpty()) {
                throw new BadRequestException("List of languages"
                        + " for deleting is empty"
                        + " or these languages don't exist.");
            }
            for (Language language : languages) {
                country.get().removeLanguage(language);
            }
            countryRepository.save(country.get());
            countryCache.put(country.get().getId(), country.get());
            return country.get();
        }
        throw new ResourceNotFoundException(NO_EXIST + " " + countryDTO.id());
    }

    @Override
    @AspectAnnotation
    public Country updateInfo(final CountryDTO countryDTO)
            throws ResourceNotFoundException {
        Optional<Country> country = countryRepository.findById(countryDTO.id());
        if (Boolean.TRUE.equals(countryRepository
                .existsByName(countryDTO.name()))) {
            throw new BadRequestException(ALREADY_EXIST
                    + " " + countryDTO.name());
        }
        if (country.isPresent()) {
            if (countryDTO.longitude() != null) {
                country.get().setLongitude(countryDTO.longitude());
            }
            if (countryDTO.latitude() != null) {
                country.get().setLatitude(countryDTO.latitude());
            }
            if (countryDTO.nationality() != null) {
                country.get().setNationality(countryDTO.nationality());
            }
            if (countryDTO.name() != null) {
                country.get().setName(countryDTO.name());
            }
            countryRepository.save(country.get());
            countryCache.put(country.get().getId(), country.get());
            return country.get();
        }
        throw new ResourceNotFoundException(NO_EXIST + " " + countryDTO.id());
    }

    @Override
    public List<Country> findCountriesWithSpecifiedLanguage(final String name)
            throws ResourceNotFoundException {
        if (Boolean.FALSE.equals(languageRepository
                .existsByName(name))) {
            throw new ResourceNotFoundException("Language don't"
                    + " exist with name = " + name);
        }
        return countryRepository
                .findAllCountriesContainingSpecifiedLanguage(name);
    }

    @Transactional
    @Override
    public void bulkInsert(List<CountryDTO> countryDTOS) {
        List<Country> countries = countryDTOS.stream()
                .map(CountryDTOUtility::buildCountryFromCountryDTO)
                .toList();

        jdbcTemplate.batchUpdate("INSERT into countries"
                        + " (country_name, nationality, latitude, longitude)"
                        + " VALUES (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setString(1, countries
                                .get(i).getName());
                        ps.setString(2, countries
                                .get(i).getNationality());
                        ps.setDouble(3, countries
                                .get(i).getLatitude());
                        ps.setDouble(4, countries
                                .get(i).getLongitude());
                    }

                    @Override
                    public int getBatchSize() {
                        return countries.size();
                    }
                });
    }

}

package com.example.geodata.service.impl;

import com.example.geodata.aspects.AspectAnnotation;
import com.example.geodata.cache.LRUCacheCountry;
import com.example.geodata.cache.LRUCacheLanguage;
import com.example.geodata.dto.LanguageDTO;
import com.example.geodata.entity.Country;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.LanguageRepository;
import com.example.geodata.service.LanguageService;
import com.example.geodata.service.utility.LanguageDTOUtility;
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
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final LRUCacheCountry countryCache;
    private final LRUCacheLanguage languageCache;
    private final JdbcTemplate jdbcTemplate;
    private static final String NO_EXIST = "Language don't exist with id =";

    @Override
    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    @Override
    @AspectAnnotation
    public Language createLanguage(final LanguageDTO languageDTO) {
        Language language = LanguageDTOUtility
                .buildLanguageFromLanguageDTO(languageDTO);
        if (languageDTO.name() == null || languageDTO.code() == null) {
            throw new BadRequestException("All fields: "
                    + "[name, code]"
                    + "must be provided.");
        }
        languageRepository.save(language);
        languageCache.put(language.getId(), language);
        return language;
    }

    @Override
    @AspectAnnotation
    public Language update(final LanguageDTO languageDTO)
            throws ResourceNotFoundException {
        Optional<Language> language = languageRepository
                .findById(languageDTO.id());
        if (language.isEmpty()) {
            throw new ResourceNotFoundException(NO_EXIST
                    + " " + languageDTO.id());
        }
        if (languageDTO.code() != null) {
            language.get().setCode(languageDTO.code());
        }
        if (languageDTO.name() != null) {
            language.get().setName(languageDTO.name());
        }
        languageRepository.save(language.get());
        for (Country country : language.get().getCountries()) {
            countryCache.remove(country.getId());
        }
        languageCache.put(language.get().getId(), language.get());
        return language.get();
    }

    @Override
    @AspectAnnotation
    public void deleteById(final Integer id)
            throws ResourceNotFoundException {
        Optional<Language> language = languageRepository.findById(id);
        if (language.isPresent()) {
            List<Integer> countriesIds = languageRepository
                    .deleteLanguageByIdAndReturnCountryIds(id);
            for (Integer countryId : countriesIds) {
                countryCache.remove(countryId);
            }
            languageCache.remove(id);
            languageRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(NO_EXIST + " " + id);
        }
    }

    @Override
    @AspectAnnotation
    public Optional<Language> findById(final Integer id)
            throws ResourceNotFoundException {
        Optional<Language> language = languageCache.get(id);
        if (language.isEmpty()) {
            language = languageRepository.findById(id);
            if (language.isEmpty()) {
                throw new ResourceNotFoundException(NO_EXIST + " " + id);
            }
            languageCache.put(id, language.get());
        }
        return language;
    }

    @Transactional
    @Override
    public void bulkInsert(List<LanguageDTO> languageDTOS) {
        List<Language> languages = languageDTOS.stream()
                .map(LanguageDTOUtility::buildLanguageFromLanguageDTO)
                .toList();
        jdbcTemplate.batchUpdate("INSERT into languages"
                        + " (language_name, language_code)"
                        + " VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i)
                            throws SQLException {
                        preparedStatement.setString(1, languages
                                .get(i).getName());
                        preparedStatement.setString(2, languages
                                .get(i).getCode());
                    }

                    @Override
                    public int getBatchSize() {
                        return languages.size();
                    }
                });
    }
}

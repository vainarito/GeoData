package com.example.geodata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.geodata.cache.LRUCacheLanguage;
import com.example.geodata.dto.LanguageDTO;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.BadRequestException;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.repository.LanguageRepository;
import com.example.geodata.service.impl.LanguageServiceImpl;
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
class LanguageServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private LRUCacheLanguage languageCache;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    void findAllLanguages() {
        List<Language> expectedLanguages = new ArrayList<>();

        when(languageRepository.findAll())
                .thenReturn(expectedLanguages);

        List<Language> actualLanguages = languageService.findAll();

        assertEquals(expectedLanguages, actualLanguages);
    }

    @Test
    void createLanguage_illegalArguments() {
        LanguageDTO languageDTO = LanguageDTO.builder()
                .build();

        assertThrows(BadRequestException.class,
                () -> languageService.createLanguage(languageDTO));
    }

    @Test
    void createLanguage_success() {
        LanguageDTO languageDTO = LanguageDTO.builder()
                .name("Russian")
                .code("RUS")
                .build();

        Language createdLanguage = languageService
                .createLanguage(languageDTO);

        assertEquals(createdLanguage.getName(), languageDTO.name());
        assertEquals(createdLanguage.getCode(), languageDTO.code());
        verify(languageRepository, times(1))
                .save(any(Language.class));
    }

    @Test
    void update_invalidId() {
        LanguageDTO languageDTO = LanguageDTO.builder()
                .id(1)
                .build();

        when(languageRepository.findById(languageDTO.id()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> languageService.update(languageDTO));
    }

    @Test
    void update_existingId()
            throws ResourceNotFoundException {
        LanguageDTO languageDTO = LanguageDTO.builder()
                .id(1)
                .name("Korean")
                .code("KNR")
                .build();

        when(languageRepository.findById(languageDTO.id()))
                .thenReturn(Optional.of(new Language()));

        Language updatedLanguage = languageService
                .update(languageDTO);

        assertEquals(updatedLanguage.getCode(), languageDTO.code());
        assertEquals(updatedLanguage.getName(), languageDTO.name());
        verify(languageRepository, times(1))
                .save(any(Language.class));
    }

    @Test
    void deleteLanguageById_invalidId() {
        int id = 1;

        when(languageRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> languageService.findById(id));
    }

    @Test
    void deleteLanguageById_existingId()
            throws ResourceNotFoundException {
        int id = 1;

        when(languageRepository.findById(id))
                .thenReturn(Optional.of(new Language()));

        languageService.deleteById(id);

        verify(languageRepository, times(1))
                .deleteById(anyInt());
    }

    @Test
    void findLanguageById_invalidId() {
        int id = 1;

        when(languageCache.get(id))
                .thenReturn(Optional.empty());
        when(languageRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> languageService.findById(id));
    }

    @Test
    void findLanguageById_existingIdAndLanguageInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<Language> expectedLanguage = Optional.of(new Language());

        when(languageCache.get(id))
                .thenReturn(expectedLanguage);

        Optional<Language> actualLanguage = languageService
                .findById(id);

        assertEquals(expectedLanguage, actualLanguage);
        verify(languageRepository, never())
                .findById(id);
    }

    @Test
    void findLanguageById_existingIdAndLanguageNotInCache()
            throws ResourceNotFoundException {
        int id = 1;
        Optional<Language> expectedLanguage = Optional.of(new Language());

        when(languageCache.get(id))
                .thenReturn(Optional.empty());
        when(languageRepository.findById(id))
                .thenReturn(expectedLanguage);

        Optional<Language> actualLanguage = languageService
                .findById(id);

        assertEquals(expectedLanguage, actualLanguage);
        verify(languageCache, times(1))
                .put(anyInt(), any(Language.class));
    }

    @Test
    void bulkInsert() {
        LanguageDTO firstLanguage = LanguageDTO.builder()
                .name("Russian")
                .code("RUS")
                .build();
        LanguageDTO secondLanguage = LanguageDTO.builder()
                .name("English")
                .code("ENG")
                .build();
        List<LanguageDTO> languageDTOS = Arrays
                .asList(firstLanguage, secondLanguage);

        languageService.bulkInsert(languageDTOS);

        verify(jdbcTemplate, times(1))
                .batchUpdate(eq("INSERT into languages"
                        + " (language_name, language_code)"
                        + " VALUES (?, ?)"), any(BatchPreparedStatementSetter.class));
    }
}
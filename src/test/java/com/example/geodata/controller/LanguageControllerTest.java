package com.example.geodata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.example.geodata.controller.LanguageController;
import com.example.geodata.dto.LanguageDTO;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.service.LanguageService;
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
class LanguageControllerTest {

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageController languageController;

    @Test
    void getAll() {
        ResponseEntity<List<Language>> responseEntity = languageController
                .getAll();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getById()
            throws ResourceNotFoundException {
        int id = 1;

        when(languageService.findById(id))
                .thenReturn(Optional.ofNullable(any(Language.class)));

        ResponseEntity<Optional<Language>> responseEntity = languageController
                .getById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void addLanguage() {
        ResponseEntity<Language> responseEntity = languageController
                .addLanguage(any(LanguageDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteLanguage()
            throws ResourceNotFoundException {
        HttpStatus httpStatus = languageController
                .deleteLanguage(anyInt());

        assertEquals(HttpStatus.OK, httpStatus);
    }

    @Test
    void updateInfo()
            throws ResourceNotFoundException {
        ResponseEntity<Language> responseEntity = languageController
                .updateInfo(any(LanguageDTO.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void bulkInsert() {
        HttpStatus httpStatus = languageController
                .bulkInsert(new ArrayList<>());

        assertEquals(HttpStatus.OK, httpStatus);
    }
}
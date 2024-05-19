package com.example.geodata.controller;

import com.example.geodata.aspects.AspectAnnotation;
import com.example.geodata.dto.LanguageDTO;
import com.example.geodata.entity.Language;
import com.example.geodata.exceptions.ResourceNotFoundException;
import com.example.geodata.service.LanguageService;
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
@Tag(name = "LanguageController")
@RestController
@RequestMapping("/api/languages")
@AllArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping("/all")
    @AspectAnnotation
    public ResponseEntity<List<Language>> getAll() {
        return new ResponseEntity<>(languageService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/info/{id}")
    @AspectAnnotation
    public ResponseEntity<Optional<Language>> getById(@PathVariable final Integer id)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(languageService.findById(id));
    }

    @PostMapping("/create")
    @AspectAnnotation
    public ResponseEntity<Language> addLanguage(
            @RequestBody final LanguageDTO languageDTO
    ) {
        return new ResponseEntity<>(languageService.createLanguage(languageDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{idLanguage}")
    @AspectAnnotation
    public HttpStatus deleteLanguage(@PathVariable final Integer idLanguage)
            throws ResourceNotFoundException {
        languageService.deleteById(idLanguage);
        return HttpStatus.OK;
    }

    @PutMapping("/updateInfo")
    @AspectAnnotation
    public ResponseEntity<Language> updateInfo(
            @RequestBody final LanguageDTO languageDTO
    )
            throws ResourceNotFoundException {
        return new ResponseEntity<>(languageService.update(languageDTO),
                HttpStatus.OK);
    }

    @PostMapping("/bulkInsert")
    @AspectAnnotation
    public HttpStatus bulkInsert(@RequestBody final List<LanguageDTO> languageDTOS) {
        languageService.bulkInsert(languageDTOS);
        return HttpStatus.OK;
    }


}

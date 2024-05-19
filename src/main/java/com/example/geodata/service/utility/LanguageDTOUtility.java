package com.example.geodata.service.utility;

import com.example.geodata.dto.LanguageDTO;
import com.example.geodata.entity.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LanguageDTOUtility {

    public Language buildLanguageFromLanguageDTO(final LanguageDTO languageDTO) {
        return Language.builder()
                .id(languageDTO.id())
                .name(languageDTO.name())
                .code(languageDTO.code())
                .build();
    }

}

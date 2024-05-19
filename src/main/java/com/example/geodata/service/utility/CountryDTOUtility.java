package com.example.geodata.service.utility;

import com.example.geodata.dto.CountryDTO;
import com.example.geodata.entity.Country;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CountryDTOUtility {

    public Country buildCountryFromCountryDTO(final CountryDTO countryDTO) {
        return Country.builder()
                .id(countryDTO.id())
                .nationality(countryDTO.nationality())
                .name(countryDTO.name())
                .latitude(countryDTO.latitude())
                .longitude(countryDTO.longitude())
                .build();
    }

}

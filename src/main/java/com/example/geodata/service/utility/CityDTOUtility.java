package com.example.geodata.service.utility;

import com.example.geodata.dto.CityDTO;
import com.example.geodata.entity.City;
import com.example.geodata.entity.Country;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CityDTOUtility {

    public City buildCityFromDTO(final CityDTO cityDTO, final Country country) {
        return City.builder()
                .id(cityDTO.id())
                .name(cityDTO.name())
                .latitude(cityDTO.latitude())
                .longitude(cityDTO.longitude())
                .country(country)
                .build();
    }

}

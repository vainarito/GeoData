package com.example.geodata.dto;

import lombok.Builder;

@Builder
public record CityDTO(Integer id, String name,
                      String countryName, Double longitude,
                      Double latitude) {

}

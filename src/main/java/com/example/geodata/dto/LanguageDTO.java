package com.example.geodata.dto;


import lombok.Builder;

@Builder
public record LanguageDTO(Integer id, String name, String code) {

}

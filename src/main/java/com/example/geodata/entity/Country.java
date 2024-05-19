package com.example.geodata.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "countries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "country_name")
    private String name;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "country",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<City> cities = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "countries_languages",
            joinColumns = {@JoinColumn(name = "country_id")},
            inverseJoinColumns = {@JoinColumn(name = "language_id")})
    @JsonManagedReference
    private Set<Language> languages = new HashSet<>();

    public void addLanguage(final Language language) {
        languages.add(language);
    }

    public void removeLanguage(final Language language) {
        languages.remove(language);
    }

    @Override
    public String toString() {
        return "id=" + id + ", " + "name=" + name + ", "
                + "nationality=" + nationality + ", " + "latitude="
                + latitude + ", " + "longitude=" + longitude;
    }

}

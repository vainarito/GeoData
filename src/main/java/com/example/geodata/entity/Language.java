package com.example.geodata.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "language_name")
    private String name;

    @Column(name = "language_code")
    private String code;

    @ManyToMany(mappedBy = "languages",
            cascade = { CascadeType.MERGE, CascadeType.PERSIST },
            fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Country> countries = new HashSet<>();

    @Override
    public String toString() {
        return "id=" + id + ", " + "name=" + name + ", " + "code=" + code;
    }

}

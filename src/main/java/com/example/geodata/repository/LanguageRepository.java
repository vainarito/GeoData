package com.example.geodata.repository;

import com.example.geodata.entity.Language;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    @Query(value = "SELECT * FROM languages obj "
            + "where obj.language_name in (?1)", nativeQuery = true)
    List<Language> findByNames(@Param("1") List<String> names);

    @Query(value = "DELETE FROM countries_languages "
            + "WHERE language_id = :languageId "
            + "RETURNING country_id", nativeQuery = true)
    List<Integer> deleteLanguageByIdAndReturnCountryIds(
            @Param("languageId") Integer languageId
    );

    Boolean existsByName(String name);

}

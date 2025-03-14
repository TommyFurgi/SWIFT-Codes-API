package com.remitly.intern_task.repository;

import com.remitly.intern_task.model.Headquarter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HeadquarterRepository extends JpaRepository<Headquarter, Integer> {
    @Query("SELECT h FROM Headquarter h WHERE h.swiftCode = :swiftCode")
    Headquarter findBySwiftCode(@Param("swiftCode") String swiftCode);

    List<Headquarter> findByCountryIso2Code(String countryISO2code);

    @Query("SELECT DISTINCT h.countryName FROM Headquarter h WHERE h.countryIso2Code = :countryIso2Code")
    List<String> findDistinctCountryNamesByIso2Code(@Param("countryIso2Code") String countryIso2Code, Pageable pageable);

}

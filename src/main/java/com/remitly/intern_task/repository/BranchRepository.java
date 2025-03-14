package com.remitly.intern_task.repository;

import com.remitly.intern_task.model.Branch;
import com.remitly.intern_task.model.Headquarter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
    @Query("SELECT b FROM Branch b WHERE b.headquarter = :headquarter")
    List<Branch> findByHeadquarterId(@Param("headquarter") Headquarter headquarter);

    @Query("SELECT b FROM Branch b WHERE b.swiftCode = :swiftCode")
    Branch findBySwiftCode(@Param("swiftCode") String swiftCode);

    List<Branch> findByCountryIso2Code(String countryISO2code);

    @Query("SELECT b FROM Branch b WHERE b.swiftCode = :swiftCode")
    List<Branch> findAllBranchesBySwiftCode(@Param("swiftCode") String swiftCode);
}

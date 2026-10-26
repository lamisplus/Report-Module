package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<Period, String> {

    @Query(value = "SELECT periodcode FROM appr_period WHERE end_date <= NOW() AND year = ?1", nativeQuery = true)
    List<String> findAllPeriodCode(Long year);

}

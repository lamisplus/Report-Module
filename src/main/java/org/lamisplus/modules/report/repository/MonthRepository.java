package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.entity.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthRepository extends JpaRepository <Month, Long>{

    @Query(value = "SELECT month_name FROM monthly_period WHERE start_date <= NOW() ORDER BY start_date DESC", nativeQuery = true)
    List<String> findAllMonth();

    Optional<Month> findByMonth(String month);
}

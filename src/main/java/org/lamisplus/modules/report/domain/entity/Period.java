package org.lamisplus.modules.report.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "appr_period", schema = "public")
public class Period {
    @Id
    @Column(name = "periodid")
    private String periodId;
    @Column(name = "periodcode")
    private String periodCode;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "year")
    private Long year;
}
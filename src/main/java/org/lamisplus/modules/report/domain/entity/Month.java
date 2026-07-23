package org.lamisplus.modules.report.domain.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "monthly_period", schema = "public")
public class Month {

    @Id
    private Long id;

    @Column(name = "month_name")
    private String month;

    @Column(name = "start_date")
    private LocalDate start;

    @Column(name = "end_date")
    private LocalDate end;
}

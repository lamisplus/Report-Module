package org.lamisplus.modules.report.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "customized_report")
public class CustomizedReport {

    @Id
    public UUID id;
    public String reportName;
    public String query;
    public LocalDate createdAt;
    public LocalDate updatedAt;
    public Boolean disabled;

    @PrePersist
    public  void prePersist () {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        id = UUID.randomUUID();
    }

    @PreUpdate
    public void preUpdate () {
        updatedAt = LocalDate.now();
    }
}

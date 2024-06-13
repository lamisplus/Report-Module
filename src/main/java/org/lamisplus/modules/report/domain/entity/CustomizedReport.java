package org.lamisplus.modules.report.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Boolean disabled;

    @PrePersist
    public  void prePersist () {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        id = UUID.randomUUID();
        disabled = false;
    }

    @PreUpdate
    public void preUpdate () {
        updatedAt = LocalDateTime.now();
    }
}

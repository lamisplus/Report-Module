package org.lamisplus.modules.report.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "details", nullable = false)
    private String details;
}

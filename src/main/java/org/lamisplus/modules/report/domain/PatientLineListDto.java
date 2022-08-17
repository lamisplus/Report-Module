package org.lamisplus.modules.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientLineListDto {
    private Long facilityId;
    private String facilityName;
    private String state;
    private String lga;
    private String patientId;
    private String hospitalNum;
    private String uniqueID;
    private String surname;
    private String otherName;
    private Date dateBirth;
    private Integer age;
    private String sex;
    private String maritalStatus;
    private String education;
    private String occupation;
    private String stateOfResidence;
    private String lgaOfResidence;
    private String address;
    private String phone;
    private Boolean archived;

    private String careEntryPoint;
    private Date dateOfConfirmedHIVTest;
    private Date dateOfRegistration;
    private String statusAtRegistration;
    private String currentStatus;
    private Date dateCurrentStatus;
    private Date artStartDate;
    private Double baselineCD4;
    private Double baselineCDP;
    private Double baselineWeight;
    private Double baselineHeight;
    private String baselineClinicStage;
    private String baselineFunctionalStatus;
    private Double systolicBP;
    private Double diastolicBP;
    private Double currentDiastolicBP;
    private Double currentSystolicBP;
    private Double currentWeight;
    private Double currentHeight;
    private String firstRegimenLine;
    private String firstRegimen;
    private String currentRegimenLine;
    private String currentRegimen;
    private Date dateOfLastRefill;
    private Date lastRefillDuration;
    private Date dateOfNextRefill;
//            "Adherence",
//            "Waist Circumference(cm)",
//            "First Regimen Line",
//            "First Regimen",
//            "First NRTI",
//            "First NNRTI",
//            "Current Regimen Line",
//            "Current Regimen",
//            "Current NRTI",
//            "Current NNRTI",
//            "Date Substituted/Switched (yyyy-mm-dd)",
//            "Date of Last Refill",
//            "Last Refill Duration (days)",
//            "Date of Next Refill (yyyy-mm-dd)",

}

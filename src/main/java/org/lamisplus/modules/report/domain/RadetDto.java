package org.lamisplus.modules.report.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class RadetDto {
	
	private String state;
	private String lga;
	private String facilityName;
	private String patientId;
	private String hospitalNum;
	private String uniqueID;
	private Date dateBirth;
	private Integer age;
	private String sex;
	private String targetGroup;
	
	//Enrolled
	private String artEnrollmentSetting;
	private String pregnancyStatus;
	//ART Commence
	private LocalDate artStartDate;
	private String regimenLineAtStart;
	private String regimenAtStart;
	//Clinic follow up  vital signs-
	private Double currentWeight;
	private Double currentHeight;
	private Double currentSystolicBP;
	
	//pharmacies
	private String currentRegimenLine;
	private String currentRegimen;
	private Integer monthOfArvRefills;
	private LocalDate lastPickupDate;
	private LocalDate iptStartDate;
	private String iptType;
	private LocalDate iptCompletedDate;
	
	private LocalDate dateOfFullDisclosure;
	private LocalDate dateOfRegimenSwitch;
	private LocalDate dateEnrolledOnOTZ;
	private LocalDate dateOfViralLoadSampleCollection;
	private LocalDate dateOfCurrentViralLoad;
	private LocalDate dateCommencedDMOC;
	private LocalDate dateOfVLResultAfterVLSampleCollection;
	private LocalDate dateOfCurrentARTStatus;
	private LocalDate dateOf3rdEACCompletion;
	private LocalDate dateOfExtendedEACCompletion;
	private LocalDate dateOfRepeatViralLoad;
	private LocalDate dateOfEacSampleCollected;
	private LocalDate dateOfReturnOfDMOC;
	private LocalDate dateOfCommencementOfEAC;
	private LocalDate dateBiometricsEnrolled;
	private LocalDate dateOfPrecancerousLesionsTreatment;
	private LocalDate dateReturnedToFacility;
	private LocalDate dateOfCervicalCancerScreening;
	private LocalDate confirmedDateOfPreviousARTStatus;
	
	private String viralLoadIndication;
	private String previousARTStatus;
	private String typeOfDMOC;
	private String caseManager;
	private String currentARTStatus;
	private String vACauseOfDead;
	private String cervicalCancerScreeningType;
	private String cervicalCancerScreeningMethod;
	private String resultOfCervicalCancerScreening;
	private String causeOfDead;
	private String precancerousLesionsTreatmentMethods;
	private String newFacility;
	
	private Integer numberOfEACSessionsCompleted;
	private Integer NumberOfOTZModulesCompleted;
	private Integer numberOfSupportGroup;
	private double currentViralLoad;
	private Double vlResultAfterVLSampleCollection;
	private String validBiometricsEnrolled;
	
	
}

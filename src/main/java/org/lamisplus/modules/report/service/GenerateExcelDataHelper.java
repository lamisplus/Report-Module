package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.hiv.domain.dto.*;
import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateExcelDataHelper {
	List<Object> errorObjects = new ArrayList<Object>();

	public static List<Map<Integer, Object>> fillPatientLineListDataMapper(@NonNull List<PatientLineDto> listFinalResult) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (PatientLineDto patient : listFinalResult) {
			if (patient != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				LocalDate vlDate = (patient.getDateOfLastViralLoad() == null)
						? null :patient.getDateOfLastViralLoad().toLocalDate();
				map.put(index++, getStringValue(String.valueOf(patient.getState())));
				map.put(index++, getStringValue(String.valueOf(patient.getLga())));
				map.put(index++, getStringValue(String.valueOf(patient.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(patient.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(patient.getPersonUuid())));
				map.put(index++, getStringValue(String.valueOf(patient.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(patient.getUniqueId())));
				map.put(index++, getStringValue(String.valueOf(patient.getSurname())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstName())));
				map.put(index++, patient.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(patient.getAge())));
				map.put(index++, getStringValue(String.valueOf(patient.getGender())));
				map.put(index++, getStringValue(String.valueOf(patient.getMaritalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getEducation())));
				map.put(index++, getStringValue(String.valueOf(patient.getOccupation())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialState())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialLga())));
				map.put(index++, getStringValue(String.valueOf(patient.getAddress() != null ? patient.getAddress().replace("\"", ""):null)));
				map.put(index++, getStringValue(String.valueOf(patient.getPhone())));
				map.put(index++, getStringValue(String.valueOf(patient.getArchived())));
				map.put(index++, getStringValue(String.valueOf(patient.getCareEntryPoint())));
				map.put(index++, (patient.getDateOfConfirmedHIVTest()));
				map.put(index++,patient.getDateOfRegistration());
				map.put(index++, getStringValue(String.valueOf(patient.getStatusAtRegistration())));
				map.put(index++, patient.getArtStartDate());
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineCD4())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineCDP())));
				map.put(index++, getStringValue(String.valueOf(patient.getSystolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getDiastolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineWeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineHeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineClinicStage())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineFunctionalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentStatus())));
				map.put(index++, patient.getDateOfCurrentStatus());
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentWeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentHeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentSystolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentDiastolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getAdherenceLevel())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstRegimen())));

				map.put(index++, getStringValue(String.valueOf(patient.getCurrentRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentRegimen())));
				map.put(index++, "");
				map.put(index++, patient.getDateOfLastRefill());
				map.put(index++, getStringValue(String.valueOf(patient.getLastRefillDuration())));
				map.put(index++, patient.getDateOfNextRefill());
				map.put(index++, getStringValue(String.valueOf(patient.getDmocType())));

				map.put(index++, patient.getDateDevolved());
				map.put(index++, getStringValue(String.valueOf(patient.getLastClinicStage())));

				map.put(index++, patient.getDateOfLastClinic());
				map.put(index++, patient.getDateOfNextClinic());
				map.put(index++, patient.getDateOfSampleCollected());
				map.put(index++, patient.getLastViralLoad());
				map.put(index++, vlDate);
				map.put(index++, patient.getViralLoadType());
				map.put(index, "");
				result.add(map);
			}
		}
		Log.info("result: " + result.size()); // going to be one
		return result;
	}

	public static List<Map<Integer, Object>> fillTBReportDataMapper(@NonNull List<TBReportProjection> tbReportProjections) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (TBReportProjection tbReportProjection : tbReportProjections) {
			if (tbReportProjection != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getState())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getLga())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getPersonUuid())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getSurname())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getFirstName())));
				map.put(index++, tbReportProjection.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getAge())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getGender())));
				map.put(index++, "");
				map.put(index++, tbReportProjection.getArtStartDate());

				map.put(index++, null);

				map.put(index++, tbReportProjection.getTbIptScreening());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbScreeningType())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbStatus())));
				map.put(index++, "");
				map.put(index++, tbReportProjection.getDateOfTbDiagnosticResultReceived());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticTestType())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticResult())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentStartDate())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentType())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentOutcome())));
				map.put(index++, tbReportProjection.getTbTreatmentCompletionDate());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getEligibleForTPT())));
				map.put(index++, tbReportProjection.getDateOfIptStart());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getRegimenName())));
				map.put(index++, tbReportProjection.getDateCompletedIpt());
				map.put(index, tbReportProjection.getWeightAtStartTpt());

				result.add(map);
			}
		}
		Log.info("result: " + result.size()); // going to be one
		return result;
	}

	public  List<Map<Integer, Object>> fillRadetDataMapper(@NonNull List<RADETDTOProjection> reportDtos, LocalDate endDate) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		deleteErrorFile();
		int sn = 1;
		Log.info("converting RADET db records to excel ....");
		RADETDTOProjection currentRecord = null;
		for (RADETDTOProjection radetReportDto : reportDtos) {
			try {
				currentRecord = radetReportDto;
				Map<Integer, Object> map = new HashMap<>();
				String personUuid = radetReportDto.getPersonUuid();
				Double repeatVl = null;
				Double currentVl = null;
				String tbStatusOutCome = null;
				int index = 0;
				boolean isCurrentVlValid = isValidResult(radetReportDto.getCurrentViralLoad());
				currentVl = isCurrentVlValid ? Double.parseDouble(radetReportDto.getCurrentViralLoad()) : null;
				boolean isRepeatValidNumber = isValidResult(radetReportDto.getRepeatViralLoadResult());
				repeatVl = isRepeatValidNumber ? Double.parseDouble(radetReportDto.getRepeatViralLoadResult()) : null;
				String treatmentMethodDate = radetReportDto.getTreatmentMethodDate();
				LocalDate treatmentMethodDateValue =  null;
				if(StringUtils.isNotBlank(treatmentMethodDate)){
					treatmentMethodDateValue =  LocalDate.parse(treatmentMethodDate);
				}
				String currentStatus = radetReportDto.getCurrentStatus();
				String previousStatus = radetReportDto.getPreviousStatus();
				if(currentStatus != null
						&& (currentStatus.contains("STOP")
						|| currentStatus.contains("Stop")
						|| currentStatus.contains("stop"))){
					currentStatus = "Stopped Treatment";
				}
				if(previousStatus != null
						&& (previousStatus.contains("STOP")
						|| previousStatus.contains("Stop")
						|| previousStatus.contains("stop"))){
					previousStatus = "Stopped Treatment";
				}
//				if(radetReportDto.getTbStatus() != null){
//					if(radetReportDto.getTbStatus().contains("No")){
//						tbStatusOutCome ="No sign or symptoms of TB";
//						//LOG.info("tbStatusOutCome {}", radetReportDto.getTbStatus());
//					}else {
//						tbStatusOutCome = "TB Suspected and referred for evaluation";
//						//LOG.info("tbStatusOutCome {}", radetReportDto.getTbStatus());
//					}
//				}

				map.put(index++, sn);
				map.put(index++, radetReportDto.getState());
				map.put(index++, radetReportDto.getLga());
				map.put(index++, radetReportDto.getLgaOfResidence());
				map.put(index++, radetReportDto.getFacilityName());
				map.put(index++, radetReportDto.getDatimId());
				map.put(index++, personUuid);
				map.put(index++, radetReportDto.getNdrPatientIdentifier());
				map.put(index++, radetReportDto.getHospitalNumber());
				map.put(index++, radetReportDto.getUniqueId());
				//ovc
				map.put(index++, radetReportDto.getHouseholdNumber());
				map.put(index++, radetReportDto.getOvcNumber());

				map.put(index++, radetReportDto.getGender());
				map.put(index++, radetReportDto.getTargetGroup());
				map.put(index++, radetReportDto.getCurrentWeight());
				map.put(index++, radetReportDto.getPregnancyStatus());
				map.put(index++, radetReportDto.getDateOfBirth());

				map.put(index++, radetReportDto.getAge());
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCareEntry())));
				map.put(index++, radetReportDto.getDateOfRegistration());
				map.put(index++, radetReportDto.getDateOfEnrollment());
				map.put(index++, radetReportDto.getArtStartDate());
				map.put(index++, radetReportDto.getLastPickupDate());
				map.put(index++, radetReportDto.getMonthsOfARVRefill());

				map.put(index++, radetReportDto.getRegimenLineAtStart());
				map.put(index++, radetReportDto.getRegimenAtStart());
				map.put(index++, radetReportDto.getDateOfCurrentRegimen());
				map.put(index++, radetReportDto.getCurrentRegimenLine());
				map.put(index++, radetReportDto.getCurrentARTRegimen());

				//cd4
				map.put(index++, radetReportDto.getCurrentClinicalStage());
				map.put(index++, radetReportDto.getDateOfLastCd4Count());
				map.put(index++, radetReportDto.getLastCd4Count());
				//vl
				map.put(index++, radetReportDto.getDateOfViralLoadSampleCollection());
				map.put(index++, radetReportDto.getDateOfCurrentViralLoadSample());
				map.put(index++, currentVl);
				map.put(index++, radetReportDto.getDateOfCurrentViralLoad());
				map.put(index++, radetReportDto.getViralLoadIndication());
				map.put(index++, radetReportDto.getVlEligibilityStatus());
				map.put(index++, radetReportDto.getDateOfVlEligibilityStatus());

				//current status
				map.put(index++, currentStatus);
				map.put(index++, radetReportDto.getCurrentStatusDate());
				map.put(index++, radetReportDto.getClientVerificationOutCome());
				map.put(index++, radetReportDto.getClientVerificationStatus());
				// map.put(index++, radetReportDto.getBiometricStatus());

				map.put(index++, radetReportDto.getCauseOfDeath());
				map.put(index++, radetReportDto.getVaCauseOfDeath());

				//previous status
				map.put(index++, previousStatus);
				map.put(index++, radetReportDto.getPreviousStatusDate());


				map.put(index++, radetReportDto.getEnrollmentSetting());
				//TB
				map.put(index++, radetReportDto.getDateOfTbScreened());
				map.put(index++, radetReportDto.getTbScreeningType());
				map.put(index++, radetReportDto.getTbStatus());
				// map.put(index++, radetReportDto.getTbStatusOutCome());
				//tb lab
				map.put(index++, radetReportDto.getDateOfTbSampleCollection());
				map.put(index++, radetReportDto.getTbDiagnosticTestType());
				map.put(index++, radetReportDto.getDateofTbDiagnosticResultReceived());
				map.put(index++, radetReportDto.getTbDiagnosticResult());

				map.put(index++, radetReportDto.getTbTreatmentStartDate());
				map.put(index++, radetReportDto.getTbTreatementType());
				map.put(index++, radetReportDto.getTbCompletionDate());
				map.put(index++, radetReportDto.getTbTreatmentOutcome());
				// map.put(index++, radetReportDto.getDateOfLastTbLam());
				// map.put(index++, radetReportDto.getTbLamResult());


				//TPT
				map.put(index++, radetReportDto.getDateOfIptStart());
				map.put(index++, radetReportDto.getIptType());
				map.put(index++, radetReportDto.getIptCompletionDate());
				map.put(index++, radetReportDto.getIptCompletionStatus());

				//EAC
				map.put(index++, radetReportDto.getDateOfCommencementOfEAC());
				map.put(index++, radetReportDto.getNumberOfEACSessionCompleted());
				map.put(index++, radetReportDto.getDateOfLastEACSessionCompleted());
				map.put(index++, radetReportDto.getDateOfExtendEACCompletion());
				map.put(index++, radetReportDto.getDateOfRepeatViralLoadEACSampleCollection());
				map.put(index++, radetReportDto.getRepeatViralLoadResult());
				map.put(index++, radetReportDto.getDateOfRepeatViralLoadResult());

				//DSD MOdel
				map.put(index++, radetReportDto.getDateOfDevolvement());
				map.put(index++, radetReportDto.getModelDevolvedTo());
				map.put(index++, radetReportDto.getDateOfCurrentDSD());
				map.put(index++, radetReportDto.getCurrentDSDModel());
				map.put(index++, null);

				//chronic care
				map.put(index++, null);
				map.put(index++, null);

				//cervicalCancerScreeningType
				map.put(index++, radetReportDto.getDateOfCervicalCancerScreening());
				map.put(index++, radetReportDto.getCervicalCancerScreeningType());
				map.put(index++, radetReportDto.getCervicalCancerScreeningMethod());
				map.put(index++, radetReportDto.getResultOfCervicalCancerScreening());
				//Precancerous
				map.put(index++, treatmentMethodDateValue);
				map.put(index++, radetReportDto.getCervicalCancerTreatmentScreened());

				// map.put(index++, radetReportDto.getLastCrytococalAntigen());
				// map.put(index++, radetReportDto.getDateOfLastCrytococalAntigen());

				//biometrics
				map.put(index++, radetReportDto.getDateBiometricsEnrolled());
				map.put(index++, radetReportDto.getNumberOfFingersCaptured());
				map.put(index++, radetReportDto.getDateBiometricsRecaptured());
				map.put(index++, radetReportDto.getNumberOfFingersRecaptured());
				// map.put(index++, null);

				//case manager
				map.put(index, radetReportDto.getCaseManager());


				result.add(map);
				sn++;
			} catch (Exception e) {
				LOG.error("An error occurred when converting db record to excel for patient id {}", currentRecord.getPersonUuid());
				writeToErrorFile(currentRecord);
				LOG.error("The error message is: " + e.getMessage());
			}
		}
		LOG.info("Done converting db records total size "+ result.size());
		return result;
	}

	private void writeToErrorFile(Object obj){
		ObjectMapper objectMapper = new ObjectMapper();
		File jsonFile = new File("radet_error.json");
		try {
			errorObjects.add(obj);
			objectMapper.writeValue(jsonFile, errorObjects);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteErrorFile(){
		File file = new File("radet_error.json");
		if(file.exists()) {
			boolean deleted = file.delete();
			if(deleted) {
				LOG.info("radet_error deleted successfully");
			} else {
				LOG.info("radet_error to delete the file");
			}
		} else {
			LOG.info("radet_error doesn't exist");
		}
	}


	private static boolean isValidResult(String value) {
		return value != null
				&& !value.isEmpty()
				&& !value.contains("-")
				&& !value.contains("+")
				&& !value.contains("<")
				&& ! value.equalsIgnoreCase("null");
	}

	public  List<Map<Integer, Object>> fillHtsDataMapper(@NonNull List<HtsReportDto> htsReportDtos) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting HTS db records to excel ....");
		try {
			for (HtsReportDto htsReportDto : htsReportDtos) {
				if (htsReportDto != null) {
					Map<Integer, Object> map = new HashMap<>();
					int index = 0;

					map.put(index++, String.valueOf(sn));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getDatimCode())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFacility())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getClientCode())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFirstName())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSurname())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getOtherName())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSex())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getTargetGroup())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getAge())));
					map.put(index++, htsReportDto.getDateOfBirth());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPhoneNumber())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getMaritalStatus())));
					map.put(index++, htsReportDto.getClientAddress() != null ? getStringValue(String.valueOf(htsReportDto.getClientAddress())).replace("\"", ""):"");
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getLgaOfResidence())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getStateOfResidence())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getEducation())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getOccupation())));
					map.put(index++, htsReportDto.getDateVisit());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFirstTimeVisit())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getNumberOfWives())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getNumberOfChildren())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIndexClient())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPreviouslyTested())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getReferredFrom())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getTestingSetting())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getModality())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCounselingType())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPregnancyStatus())));
//					map.put(index++, getStringValue(String.valueOf(htsReportDto.getBreastFeeding())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIndexType())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFinalHivTestResult())));
					map.put(index++, htsReportDto.getDateOfHivTesting());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIfRecencyTestingOptIn())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getRecencyId())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getRecencyTestType())));
					map.put(index++, htsReportDto.getRecencyTestDate());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getRecencyInterpretation())));
					map.put(index++, htsReportDto.getViralLoadSampleCollectionDate());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getViralLoadResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getViralLoadConfirmationResult())));
//					map.put(index++, htsReportDto.getViralLoadConfirmationDate());
					map.put(index++, htsReportDto.getViralLoadReceivedResultDate());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFinalRecencyResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getAssessmentCode())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSyphilisTestResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getHepatitisBTestResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getHepatitisCTestResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCd4Type())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCd4TestResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPrepOffered())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPrepAccepted())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getNumberOfCondomsGiven())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getNumberOfLubricantsGiven())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getHtsLatitude())));
					map.put(index, getStringValue(String.valueOf(htsReportDto.getHtsLongitude())));
					result.add(map);
					sn++;
				}
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}


	public  List<Map<Integer, Object>> fillPrepDataMapper(@NonNull List<PrepReportDto> prepReportDtos) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting PrEP db records to excel ....");
		try {
			for (PrepReportDto prepReportDto : prepReportDtos) {
				if (prepReportDto != null) {
					Map<Integer, Object> map = new HashMap<>();
					int index = 0;

					map.put(index++, String.valueOf(sn));
					map.put(index++, String.valueOf(String.valueOf(prepReportDto.getDatimId())));
					map.put(index++, String.valueOf(String.valueOf(prepReportDto.getState())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getLga())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getFacilityName())));
					map.put(index++, String.valueOf(String.valueOf(prepReportDto.getPersonUuid())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getHospitalNumber())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getFirstName())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getSurname())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getOtherName())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getSex())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getTargetGroup())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getAge())));
					map.put(index++,prepReportDto.getDateOfBirth());
					map.put(index++,getStringValue(String.valueOf(prepReportDto.getPhone())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getMaritalStatus())));
					map.put(index++, getStringValue(prepReportDto.getAddress() != null?prepReportDto.getAddress().replace("\"", ""):""));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getResidentialLga())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getResidentialState())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getEducation())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getOccupation())));
					map.put(index++, prepReportDto.getDateOfRegistration());
					map.put(index++, prepReportDto.getPrepCommencementDate());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaseLineRegimen())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getPrepType())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getPrepDistributionSetting())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaselineSystolicBp())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaselineDiastolicBp())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaselineWeight())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaselineHeight())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaseLineCreatinine())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaseLineHepatitisB())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaseLineHepatitisC())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getHIVStatusAtPrepInitiation())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getBaseLineUrinalysis())));
					map.put(index++, prepReportDto.getBaseLineUrinalysisDate());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getIndicationForPrep())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentRegimen())));
					map.put(index++, prepReportDto.getDateOfLastPickup());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentStatus())));
					map.put(index++, prepReportDto.getDateOfCurrentStatus());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentSystolicBp())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentDiastolicBp())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentWeight())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentHeight())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentHIVStatus())));
					map.put(index++, prepReportDto.getDateOfCurrentHIVStatus());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getCurrentUrinalysis())));
					map.put(index++, prepReportDto.getCurrentUrinalysisDate());
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getPregnancyStatus())));
					map.put(index++, getStringValue(String.valueOf(prepReportDto.getInterruptionReason())));
					map.put(index++, prepReportDto.getInterruptionDate());
					map.put(index, prepReportDto.getHivEnrollmentDate());

					result.add(map);
					sn++;
				}
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}


	public static List<Map<Integer, Object>> fillBiometricDataMapper(@NonNull List<BiometricReportDto> biometrics) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting Biometric db records to excel ....");
		try {
			for (BiometricReportDto dto : biometrics) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, dto.getState());
				map.put(index++, dto.getFacilityName());
				map.put(index++, dto.getDatimId());
				map.put(index++, dto.getHospitalNum());
				map.put(index++, getStringValue(dto.getName()));
				map.put(index++, dto.getDateBirth());
				map.put(index++, getStringValue(String.valueOf(dto.getAge())));
				map.put(index++, dto.getSex());
				map.put(index++, dto.getAddress());
				map.put(index++, dto.getPhone());
				map.put(index++, dto.getEnrollDate());
				map.put(index++, getStringValue(String.valueOf(dto.getFingers())));
				map.put(index, dto.getValid());
				result.add(map);
				sn++;
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
		}
		return result;
	}

	private static String getStringValue(String value) {
		return value.replace("null", "");
	}

	public static List<Map<Integer, Object>> fillClinicDataMapper(
			@NonNull List<ClinicDataDto> clinicDataDtos) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting Clinic db records to excel ....");
		try {
			for (ClinicDataDto clinicDataDto : clinicDataDtos) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getPatientId())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getHospitalNumber())));
				map.put(index++, clinicDataDto.getVisitDate());
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getClinicalStage())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getFunctionalStatus())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getTbStatus())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getBodyWeight())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getHeight())));
				if( clinicDataDto.getSystolic() != null &&  clinicDataDto.getDiastolic() != null){
					map.put(index++, clinicDataDto.getSystolic() + "/"+ clinicDataDto.getDiastolic());
				}else {
					map.put(index++, null);
				}
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getSystolic())));
				map.put(index++, getStringValue(String.valueOf(clinicDataDto.getDiastolic())));
				map.put(index++,  getStringValue(String.valueOf(clinicDataDto.getPregnancyStatus())));
				map.put(index,    clinicDataDto.getNextAppointment());
				result.add(map);
				sn++;
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
		}
		return result;
	}

	public static List<Map<Integer, Object>> fillPharmacyDataMapper(
			@NonNull List<PharmacyReport> pharmacies) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting Pharmacy db records to excel ....");
		try {
			for (PharmacyReport pharmacy : pharmacies) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getPatientId())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getHospitalNum())));
				map.put(index++, pharmacy.getDateVisit());
				map.put(index++, getStringValue(String.valueOf(pharmacy.getRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getRegimens())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getRefillPeriod())));
				map.put(index++, getStringValue(String.valueOf(pharmacy.getMmdType())));
				map.put(index++, pharmacy.getNextAppointment());
				map.put(index, pharmacy.getDsdModel());
				result.add(map);
				sn++;
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
		}
		return result;
	}

	public static List<Map<Integer, Object>> fillLabDataMapper(
			@NonNull List<LabReport> labReports) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting Laboratory db records to excel ....");
		try {
			for (LabReport labReport : labReports) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, getStringValue(String.valueOf(labReport.getFacilityId())));
				map.put(index++, getStringValue(String.valueOf(labReport.getFacility())));
				map.put(index++, getStringValue(String.valueOf(labReport.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(labReport.getPatientId())));
				map.put(index++, getStringValue(String.valueOf(labReport.getHospitalNum())));
				map.put(index++, getStringValue(String.valueOf(labReport.getTest())));
				map.put(index++, labReport.getSampleCollectionDate());
				map.put(index++, getStringValue(String.valueOf(labReport.getResult())));
				map.put(index, labReport.getDateReported());
				result.add(map);
				sn++;
			}
			LOG.info("Done converting db records total size {}", result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error("The error message is: " + e.getMessage());
		}
		return result;
	}

	public static List<Map<Integer, Object>> fillClientServiceListDataMapper(@NonNull List<ClientServiceDto> listFinalResult) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (ClientServiceDto clientService : listFinalResult) {
			if (clientService != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				map.put(index++, getStringValue(clientService.getFacilityState()));
				map.put(index++, getStringValue(clientService.getFacilityName()));
				map.put(index++, getStringValue(clientService.getSerialEnrollmentNo()));
				map.put(index++, getStringValue(clientService.getPersonUuid()));
				map.put(index++, getStringValue(String.valueOf(clientService.getDateOfObservation())));
				map.put(index++, getStringValue(clientService.getAnyOfTheFollowingList()));
				map.put(index++, getStringValue(clientService.getDateOfAttempt()));
				map.put(index++, getStringValue(clientService.getVerificationAttempts()));
				map.put(index++, getStringValue(clientService.getVerificationStatus()));
				map.put(index++, getStringValue(clientService.getOutcome()));
				map.put(index++, getStringValue(clientService.getDsdModel()));
				map.put(index++, getStringValue(clientService.getComment()));
				map.put(index++, getStringValue(clientService.getReturnedToCare()));
				map.put(index++, getStringValue(clientService.getReferredTo()));
				map.put(index++, getStringValue(clientService.getDiscontinuation()));
				map.put(index++, getStringValue(clientService.getDateOfDiscontinuation()));
				map.put(index++, getStringValue(clientService.getReasonForDiscontinuation()));

				result.add(map);
			}
		}
		Log.info("result: " + result.size()); // going to be one
		return result;
	}



}

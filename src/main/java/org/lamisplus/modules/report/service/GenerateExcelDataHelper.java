package org.lamisplus.modules.report.service;

import lombok.NonNull;

import lombok.RequiredArgsConstructor;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.hiv.domain.dto.*;
import org.lamisplus.modules.hiv.repositories.HIVStatusTrackerRepository;
import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.*;


@Component
@RequiredArgsConstructor
public class GenerateExcelDataHelper {
	
	private  final StatusManagementService statusManagementService;
	
	private final HIVStatusTrackerRepository statusTrackerRepository;
	
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
				map.put(index++,patient.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(patient.getAge())));
				map.put(index++, getStringValue(String.valueOf(patient.getGender())));
				map.put(index++, getStringValue(String.valueOf(patient.getMaritalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getEducation())));
				map.put(index++, getStringValue(String.valueOf(patient.getOccupation())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialState())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialLga())));
				map.put(index++, getStringValue(String.valueOf(patient.getAddress())));
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
		Log.info("result: " + result.size());
		return result;
	}
	
	
	public  List<Map<Integer, Object>> fillRadetDataMapper(@NonNull List<RadetReportDto> radetReportDtoList, LocalDate endDate) {
			List<Map<Integer, Object>> result = new ArrayList<>();
			int sn = 1;
			for (RadetReportDto radetReportDto : radetReportDtoList) {
				Map<Integer, Object> map = new HashMap<>();
				String personUuid = radetReportDto.getPersonUuid();
				HIVStatusDisplay currentStatus =  null;
				HIVStatusDisplay previousStatus = null;
				Deque<HIVStatusDisplay> currentAndPreviousClientStatus =
						statusManagementService.getCurrentAndPreviousClientStatus(personUuid, endDate);
				if(currentAndPreviousClientStatus.size() > 1){
					currentStatus = currentAndPreviousClientStatus.pop();
					previousStatus = currentAndPreviousClientStatus.pop();
				}else if (currentAndPreviousClientStatus.size() == 1) {
					currentStatus = currentAndPreviousClientStatus.pop();
					
				}
				LocalDate iptCompletionDate = radetReportDto.getIptCompletionDate();
				if (iptCompletionDate != null) {
					if(iptCompletionDate.isAfter(endDate)){
						iptCompletionDate = null;
					}
				}
				int index = 0;
				map.put(index++,getStringValue(String.valueOf(sn)));
				map.put(index++, radetReportDto.getState());
				map.put(index++, radetReportDto.getLga());
				map.put(index++, radetReportDto.getFacilityName());
				map.put(index++, radetReportDto.getDatimId());
				map.put(index++, personUuid);
				map.put(index++, radetReportDto.getHospitalNumber());
				//ovc
				map.put(index++, radetReportDto.getHouseholdNumber());
				map.put(index++,  radetReportDto.getOvcNumber());
				
				map.put(index++, radetReportDto.getGender());
				map.put(index++, radetReportDto.getTargetGroup());
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentWeight())));
				map.put(index++, radetReportDto.getPregnancyStatus());
				map.put(index++, radetReportDto.getDateOfBirth());
				
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getAge())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCareEntry())));
				
				map.put(index++,radetReportDto.getArtStartDate());
				map.put(index++, radetReportDto.getLastPickupDate());
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getMonthsOfARVRefill())));
				
				
				map.put(index++, radetReportDto.getRegimenLineAtStart());
				map.put(index++, radetReportDto.getRegimenAtStart());
				map.put(index++, radetReportDto.getDateOfCurrentRegimen());
				map.put(index++, radetReportDto.getCurrentARTRegimen());
				map.put(index++, radetReportDto.getCurrentRegimenLine());
				
				
				map.put(index++, radetReportDto.getCurrentClinicalStage());
				map.put(index++, radetReportDto.getDateOfLastCd4Count());
				map.put(index++,radetReportDto.getLastCd4Count());
				//vl
				map.put(index++, radetReportDto.getDateOfViralLoadSampleCollection());
				map.put(index++, radetReportDto.getCurrentViralLoad());
				map.put(index++, radetReportDto.getDateOfCurrentViralLoad());
				map.put(index++, radetReportDto.getViralLoadIndication());
				//current status
				if(currentStatus != null) {
					map.put(index++, currentStatus.getDescription());
					map.put(index++, currentStatus.getDate());
				}else {
					map.put(index++, null);
					map.put(index++, null);
				}
				if(currentStatus != null && currentStatus.getDescription().contains("DIED")){
					String causeOfDeath =
							statusTrackerRepository.getCauseOfDeathByPersonUuid(radetReportDto.getPersonUuid());
					map.put(index++, causeOfDeath);
				}else {
					map.put(index++, null);
				}
				//previous status
				if(previousStatus != null) {
					map.put(index++, previousStatus.getDescription());
					map.put(index++, previousStatus.getDate());
				}else {
					map.put(index++, null);
					map.put(index++, null);
				}
				
				map.put(index++, radetReportDto.getEnrollmentSetting());
				//TB
				map.put(index++,radetReportDto.getTbStatus());
				map.put(index++,radetReportDto.getDateOfTbScreened());
				map.put(index++,radetReportDto.getTbStatus());
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				map.put(index++,null);
				//TPT
				map.put(index++, radetReportDto.getDateOfIptStart());
				map.put(index++, radetReportDto.getIptType());
				map.put(index++, iptCompletionDate);
				map.put(index++,null);
				
				//EAC
				map.put(index++, radetReportDto.getDateOfCommencementOfEAC());
				map.put(index++,getStringValue(String.valueOf(radetReportDto.getNumberOfEACSessionCompleted())));
				map.put(index++, radetReportDto.getDateOfLastEACSessionCompleted());
				map.put(index++, radetReportDto.getDateOfExtendEACCompletion());
				map.put(index++, radetReportDto.getDateOfRepeatViralLoadEACSampleCollection());
				map.put(index++, radetReportDto.getRepeatViralLoadResult());
				map.put(index++, radetReportDto.getDateOfRepeatViralLoadResult());
				//DSD MOdel
				map.put(index++,null);
				map.put(index++,null);
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
				map.put(index++,null);
				map.put(index++,null);
				//biometrics
				map.put(index++, radetReportDto.getDateBiometricsEnrolled());
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getNumberOfFingersCaptured())));
				map.put(index, null);
				result.add(map);
				sn++;
			}
		return result;
	}
	
//	private HIVStatusDisplay getCurrentClientStatus(String personUuid, Quarter currentQuarter) {
//		return statusManagementService.getClientReportingStatus(personUuid, currentQuarter.getEnd());
//	}
	
//	private  int calculatePreviousStatus(Map<Integer, Object> map, HIVStatusDisplay previousQuarterStatus,RadetReportDto radetReportDto,
//	                                     EnrollmentStatus enrollmentStatus, int index) {
//		if (previousQuarterStatus != null) {
//			Quarter previousQuarter = statusManagementService
//					.getPreviousQuarter(previousQuarterStatus.getDate().minusDays(10));
//			HIVStatusDisplay previousQuarterStatus1 =
//					statusManagementService.getClientPreviousInternalQuarterStatus(previousQuarter.getEnd(), radetReportDto.getPersonUuid());
//			boolean isActiveRestart = previousQuarterStatus1 != null
//					&& previousQuarterStatus1.getDescription() != null
//					&& previousQuarterStatus1.getDescription().equalsIgnoreCase("IIT")
//					&& previousQuarterStatus.getDescription().equalsIgnoreCase("ACTIVE");
//			if (isActiveRestart) {
//				map.put(index++, "ACTIVE-RESTART");
//				map.put(index++, previousQuarterStatus.getDate());
//				return index;
//			}
//			boolean isTransferIn = enrollmentStatus != null
//					&& enrollmentStatus.getHivEnrollmentStatus() != null
//					&& enrollmentStatus.getHivEnrollmentStatus().contains("In")
//					&& previousQuarterStatus.getDescription().equalsIgnoreCase("ACTIVE");
//			if (isTransferIn) {
//			map.put(index++, "TRANSFER-IN");
//				map.put(index++, previousQuarterStatus.getDate());
//				return index;
//			}
//			map.put(index++, previousQuarterStatus.getDescription());
//			map.put(index++, previousQuarterStatus.getDate());
//			return index;
//		}else {
//			map.put(index++, radetReportDto.getCurrentStatus());
//			map.put(index++, radetReportDto.getDateOfCurrentStatus());
//			return index;
//		}
//	}
//	private static int processAndSetCurrentStatus(
//			RadetReportDto radetReportDto,
//			Map<Integer, Object> map,
//			HIVStatusDisplay previousQuarterStatus,
//			EnrollmentStatus enrollmentStatus,
//			int index) {
//		boolean isRestart = previousQuarterStatus != null
//				&& previousQuarterStatus.getDescription() != null
//				&& previousQuarterStatus.getDescription().contains("IIT")
//				&& radetReportDto.getCurrentStatus() != null
//				&& radetReportDto.getCurrentStatus().contains("ACTIVE");
//		if(isRestart){
//			map.put(index++, "ACTIVE-RESTART");
//			return index;
//		}
//		boolean isTransferIn =
//				enrollmentStatus != null
//				&& enrollmentStatus.getHivEnrollmentStatus() != null
//				&& enrollmentStatus.getHivEnrollmentStatus().contains("In")
//				&& radetReportDto.getCurrentStatus().equalsIgnoreCase("ACTIVE");
//		if(isTransferIn){
//			map.put(index++,"ACTIVE-TRANSFER-IN");
//			return index;
//
//		}
//		map.put(index++, radetReportDto.getCurrentStatus());
//		return index;
//	}
//
	public static List<Map<Integer, Object>> fillBiometricDataMapper(@NonNull List<BiometricReportDto> biometrics) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
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
			map.put(index++, dto.getEnrollDate());
			map.put(index++, getStringValue(String.valueOf(dto.getFingers())));
			map.put(index, dto.getValid());
			result.add(map);
			sn++;
		}
		return result;
	}
	
	private static String getStringValue(String value) {
		return value.replace("null", "");
	}
	
	
	public static List<Map<Integer, Object>> fillPharmacyDataMapper(
			@NonNull List<PharmacyReport> pharmacies) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
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
		return result;
	}
	
	
	public static List<Map<Integer, Object>> fillLabDataMapper(
			@NonNull List<LabReport> labReports) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
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
		return result;
	}
	
	
	
	
}

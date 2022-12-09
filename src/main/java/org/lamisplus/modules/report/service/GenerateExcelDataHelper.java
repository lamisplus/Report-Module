package org.lamisplus.modules.report.service;

import lombok.NonNull;

import org.audit4j.core.util.Log;
import org.lamisplus.modules.hiv.domain.dto.LabReport;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.hiv.domain.dto.PharmacyReport;
import org.lamisplus.modules.hiv.domain.dto.RadetReportDto;
import org.lamisplus.modules.report.domain.BiometricReportDto;



import java.util.*;

public class GenerateExcelDataHelper {
	
	public static List<Map<Integer, String>> fillPatientLineListDataMapper(@NonNull List<PatientLineDto> listFinalResult) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (PatientLineDto patient : listFinalResult) {
			if (patient != null) {
				Map<Integer, String> map = new HashMap<>();
				int index = 0;
				String vlDate = (patient.getDateOfLastViralLoad() == null)
						? " ":patient.getDateOfLastViralLoad().toLocalDate().toString();
				map.put(index++, getStringValue(String.valueOf(patient.getState())));
				map.put(index++, getStringValue(String.valueOf(patient.getLga())));
				map.put(index++, getStringValue(String.valueOf(patient.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(patient.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(patient.getPersonUuid())));
				map.put(index++, getStringValue(String.valueOf(patient.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(patient.getUniqueId())));
				map.put(index++, getStringValue(String.valueOf(patient.getSurname())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstName())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfBirth())));
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
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfConfirmedHIVTest())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfRegistration())));
				map.put(index++, getStringValue(String.valueOf(patient.getStatusAtRegistration())));
				map.put(index++, getStringValue(String.valueOf(patient.getArtStartDate())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineCD4())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineCDP())));
				map.put(index++, getStringValue(String.valueOf(patient.getSystolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getDiastolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineWeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineHeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineClinicStage())));
				map.put(index++, getStringValue(String.valueOf(patient.getBaselineFunctionalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfCurrentStatus())));
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
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastRefill())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastRefillDuration())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextRefill())));
				map.put(index++, getStringValue(String.valueOf(patient.getDmocType())));
				
				map.put(index++, getStringValue(String.valueOf(patient.getDateDevolved())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastClinicStage())));
				
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastClinic())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextClinic())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfSampleCollected())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastViralLoad())));
				map.put(index++, getStringValue(vlDate));
				map.put(index++, getStringValue(String.valueOf(patient.getViralLoadType())));
				map.put(index, "");
				result.add(map);
			}
		}
		Log.info("result: " + result.size());
		return result;
	}
	
	
	public static List<Map<Integer, String>> fillRadetDataMapper(@NonNull List<RadetReportDto> radetReportDtoList) {
			List<Map<Integer, String>> result = new ArrayList<>();
			int sn = 1;
			for (RadetReportDto radetReportDto : radetReportDtoList) {
				Map<Integer, String> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getState())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getLga())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getPersonUuid())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getGender())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getTargetGroup())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentWeight())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getPregnancyStatus())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfBirth())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getAge())));
				
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getArtStartDate())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getLastPickupDate())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getMonthsOfARVRefill())));
				
				
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getRegimenLineAtStart())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getRegimenAtStart())));
				map.put(index++,"");
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentARTRegimen())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentRegimenLine())));
				
				
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentClinicalStage())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfLastCd4Count())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getLastCd4Count())));
				//vl
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfViralLoadSampleCollection())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentViralLoad())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfCurrentViralLoad())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getViralLoadIndication())));
				//status
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCurrentStatus())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfCurrentStatus())));
				//previous status
				map.put(index++, "");
				map.put(index++, "");
				
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getEnrollmentSetting())));
				//TB
				map.put(index++,getStringValue(String.valueOf(radetReportDto.getTbStatus())));
				map.put(index++,getStringValue(String.valueOf(radetReportDto.getDateOfTbScreened())));
				map.put(index++,getStringValue(String.valueOf(radetReportDto.getTbStatus())));
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				//TPT
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++,"");
				
				//EAC
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfCommencementOfEAC())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getNumberOfEACSessionCompleted())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfLastEACSessionCompleted())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfExtendEACCompletion())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfRepeatViralLoadEACSampleCollection())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getRepeatViralLoadResult())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateOfRepeatViralLoadResult())));
				
				//DSD MOdel
				map.put(index++,"");
				map.put(index++,"");
				map.put(index++, "");
				//chronic care
				map.put(index++, "");
				map.put(index++, "");
				//cervicalCancerScreeningType
				map.put(index++, "");
				map.put(index++, "");
				map.put(index++, "");
				map.put(index++, "");
				//Precancerous
				map.put(index++,"");
				map.put(index++,"");
				//biometrics
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getDateBiometricsEnrolled())));
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getNumberOfFingersCaptured())));
				map.put(index, "");
				result.add(map);
				sn++;
			}
		return result;
	}
	
	public static List<Map<Integer, String>> fillBiometricDataMapper(@NonNull List<BiometricReportDto> biometrics) {
		List<Map<Integer, String>> result = new ArrayList<>();
		int sn = 1;
		for (BiometricReportDto dto : biometrics) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(sn));
			map.put(index++, dto.getState());
			map.put(index++, dto.getFacilityName());
			map.put(index++, dto.getDatimId());
			map.put(index++, dto.getHospitalNum());
			map.put(index++, getStringValue(dto.getName()));
			map.put(index++, getStringValue(String.valueOf(dto.getDateBirth())));
			map.put(index++, getStringValue(String.valueOf(dto.getAge())));
			map.put(index++, dto.getSex());
			map.put(index++, dto.getAddress());
			map.put(index++, getStringValue(String.valueOf(dto.getEnrollDate())));
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
	
	
	public static List<Map<Integer, String>> fillPharmacyDataMapper(
			@NonNull List<PharmacyReport> pharmacies) {
		List<Map<Integer, String>> result = new ArrayList<>();
		int sn = 1;
		for (PharmacyReport pharmacy : pharmacies) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(sn));
			//map.put(index++, getStringValue(String.valueOf(pharmacy.getFacilityId())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getFacilityName())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getDatimId())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getPatientId())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getHospitalNum())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getDateVisit())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getRegimenLine())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getRegimens())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getRefillPeriod())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getMmdType())));
			map.put(index++, getStringValue(String.valueOf(pharmacy.getNextAppointment())));
			map.put(index, pharmacy.getDsdModel());
			result.add(map);
			sn++;
		}
		return result;
	}
	
	
	public static List<Map<Integer, String>> fillLabDataMapper(
			@NonNull List<LabReport> labReports) {
		List<Map<Integer, String>> result = new ArrayList<>();
		int sn = 1;
		for (LabReport labReport : labReports) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(sn));
			map.put(index++, getStringValue(String.valueOf(labReport.getFacilityId())));
			map.put(index++, getStringValue(String.valueOf(labReport.getFacility())));
			map.put(index++, getStringValue(String.valueOf(labReport.getDatimId())));
			map.put(index++, getStringValue(String.valueOf(labReport.getPatientId())));
			map.put(index++, getStringValue(String.valueOf(labReport.getHospitalNum())));
			map.put(index++, getStringValue(String.valueOf(labReport.getTest())));
			map.put(index++, getStringValue(String.valueOf(labReport.getSampleCollectionDate())));
			map.put(index++, getStringValue(String.valueOf(labReport.getResult())));
			map.put(index, getStringValue(String.valueOf(labReport.getDateReported())));
			result.add(map);
			sn++;
		}
		return result;
	}
	
	
	
	
}

package org.lamisplus.modules.report.service;

import lombok.NonNull;

import org.audit4j.core.util.Log;
import org.lamisplus.modules.hiv.domain.dto.LabReport;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.hiv.domain.dto.PharmacyReport;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.lamisplus.modules.report.domain.RadetDto;


import java.util.*;

public class GenerateExcelDataHelper {
	
	public static List<Map<Integer, String>> fillPatientLineListDataMapper(@NonNull List<PatientLineDto> listFinalResult) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (PatientLineDto patient : listFinalResult) {
			if (patient != null) {
				Map<Integer, String> map = new HashMap<>();
				int index = 0;
				//map.put(index++, getStringValue(String.valueOf(patient.getFacilityId())));
				map.put(index++, getStringValue(String.valueOf(patient.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(patient.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(patient.getLga())));
				map.put(index++, getStringValue(String.valueOf(patient.getState())));
				map.put(index++, getStringValue(String.valueOf(patient.getPersonUuid())));
				map.put(index++, getStringValue(String.valueOf(patient.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(patient.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(patient.getSurname())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstName())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfBirth())));
				map.put(index++, getStringValue(String.valueOf(patient.getAge())));
				map.put(index++, getStringValue(String.valueOf(patient.getGender())));
				map.put(index++, getStringValue(String.valueOf(patient.getMaritalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getEducation())));
				map.put(index++, getStringValue(String.valueOf(patient.getOccupation())));
				map.put(index++, getStringValue(String.valueOf(patient.getStateOfResidence())));
				map.put(index++, getStringValue(String.valueOf(patient.getLgaOfResidence())));
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
				map.put(index++, getStringValue(String.valueOf(patient.getDateCurrentStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentWeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentHeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentSystolicBP())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentDiastolicBP())));
				map.put(index++, "");
				map.put(index++, getStringValue(String.valueOf(patient.getFirstRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(patient.getFirstRegimen())));
				
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentRegimen())));
				map.put(index++, "");
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastRefill())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastRefillDuration())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextRefill())));
				map.put(index++, getStringValue(String.valueOf(patient.getDmocType())));
				;
				map.put(index++, getStringValue(String.valueOf(patient.getDateDevolved())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastClinicStage())));
				;
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastClinic())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextClinic())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfSampleCollected())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastViralLoad())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastViralLoad())));
				map.put(index++, getStringValue(String.valueOf(patient.getViralLoadType())));
				map.put(index, "");
				result.add(map);
			}
		}
		Log.info("result: " + result.size());
		return result;
	}
	
	
	public static List<Map<Integer, String>> fillRadetDataMapper(@NonNull Set<RadetDto> radetDtoSet) {
			List<Map<Integer, String>> result = new ArrayList<>();
			int sn = 1;
			for (RadetDto patient : radetDtoSet) {
				Map<Integer, String> map = new HashMap<>();
				int index = 0;
				map.put(index++, String.valueOf(sn));
				map.put(index++, patient.getFacilityName());
				map.put(index++, patient.getLga());
				map.put(index++, patient.getState());
				map.put(index++, patient.getPatientId());
				map.put(index++, patient.getHospitalNum());
				map.put(index++, patient.getUniqueID());
				map.put(index++, getStringValue(String.valueOf(patient.getDateBirth())));
				map.put(index++, getStringValue(String.valueOf(patient.getAge())));
				map.put(index++, patient.getSex());
				map.put(index++, getStringValue(String.valueOf(patient.getArtStartDate())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentWeight())));
				map.put(index++, getStringValue(String.valueOf(patient.getLastPickupDate())));
				map.put(index++, getStringValue(String.valueOf(patient.getMonthOfArvRefills())));
				map.put(index++, getStringValue(String.valueOf(patient.getIptStartDate())));
				map.put(index++, patient.getIptType());
				map.put(index++, getStringValue(String.valueOf(patient.getIptCompletedDate())));
				map.put(index++, patient.getRegimenLineAtStart());
				map.put(index++, patient.getRegimenAtStart());
				map.put(index++, patient.getCurrentRegimenLine());
				map.put(index++, patient.getCurrentRegimen());
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfRegimenSwitch())));
				map.put(index++, patient.getPregnancyStatus());
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfFullDisclosure())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateEnrolledOnOTZ())));
				map.put(index++, getStringValue(String.valueOf(patient.getNumberOfSupportGroup())));
				map.put(index++, getStringValue(String.valueOf(patient.getNumberOfOTZModulesCompleted())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfViralLoadSampleCollection())));
				map.put(index++, String.valueOf(patient.getCurrentViralLoad()));
				map.put(index++, String.valueOf(patient.getDateOfCurrentViralLoad()));
				map.put(index++, patient.getViralLoadIndication());
				map.put(index++, getStringValue(String.valueOf(patient.getVlResultAfterVLSampleCollection())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfVLResultAfterVLSampleCollection())));
				map.put(index++, patient.getPreviousARTStatus());
				map.put(index++, getStringValue(String.valueOf(patient.getConfirmedDateOfPreviousARTStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getCurrentARTStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfCurrentARTStatus())));
				map.put(index++, patient.getCauseOfDead());
				map.put(index++, patient.getVACauseOfDead());
				map.put(index++, patient.getNewFacility());
				map.put(index++, patient.getArtEnrollmentSetting());
				map.put(index++, getStringValue(String.valueOf(patient.getDateCommencedDMOC())));
				map.put(index++, patient.getTypeOfDMOC());
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfReturnOfDMOC())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfCommencementOfEAC())));
				map.put(index++, getStringValue(String.valueOf(patient.getNumberOfEACSessionsCompleted())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOf3rdEACCompletion())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfExtendedEACCompletion())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfRepeatViralLoad())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfCervicalCancerScreening())));
				map.put(index++, patient.getCervicalCancerScreeningType());
				map.put(index++, patient.getCervicalCancerScreeningMethod());
				map.put(index++, patient.getResultOfCervicalCancerScreening());
				map.put(index++, getStringValue(String.valueOf(patient.getDateOfPrecancerousLesionsTreatment())));
				map.put(index++, getStringValue(String.valueOf(patient.getDateReturnedToFacility())));
				map.put(index++, patient.getPrecancerousLesionsTreatmentMethods());
				map.put(index++, getStringValue(String.valueOf(patient.getDateBiometricsEnrolled())));
				map.put(index++, patient.getValidBiometricsEnrolled());
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
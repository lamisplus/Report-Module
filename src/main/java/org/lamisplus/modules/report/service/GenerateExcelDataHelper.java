package org.lamisplus.modules.report.service;

import lombok.NonNull;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.domain.RadetDto;

import java.util.*;

public class GenerateExcelDataHelper {
	
	public static List<Map<Integer, String>> fillPatientLineListDataMapper(@NonNull List<PatientLineListDto> listFinalResult) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (PatientLineListDto patient : listFinalResult) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(patient.getFacilityId()));
			map.put(index++, patient.getFacilityName());
			map.put(index++, patient.getLga());
			map.put(index++, patient.getState());
			map.put(index++, patient.getPatientId());
			map.put(index++, patient.getHospitalNum());
			map.put(index++, patient.getUniqueID());
			map.put(index++, patient.getSurname());
			map.put(index++, patient.getOtherName());
			map.put(index++, String.valueOf(patient.getDateBirth()));
			map.put(index++, String.valueOf(patient.getAge()));
			map.put(index++, patient.getSex());
			map.put(index++, patient.getMaritalStatus());
			map.put(index++, patient.getEducation());
			map.put(index++, patient.getOccupation());
			map.put(index++, patient.getStateOfResidence());
			map.put(index++, patient.getLgaOfResidence());
			map.put(index++, patient.getAddress());
			map.put(index++, patient.getPhone());
			map.put(index++, String.valueOf(patient.getArchived()));
			map.put(index++, patient.getCareEntryPoint());
			map.put(index++, String.valueOf(patient.getDateOfConfirmedHIVTest()));
			map.put(index++, String.valueOf(patient.getDateOfRegistration()));
			map.put(index++, patient.getStatusAtRegistration());
			map.put(index++, String.valueOf(patient.getArtStartDate()));
			map.put(index++, String.valueOf(patient.getBaselineCD4()));
			map.put(index++, String.valueOf(patient.getBaselineCDP()));
			map.put(index++, String.valueOf(patient.getSystolicBP()));
			map.put(index++, String.valueOf(patient.getDiastolicBP()));
			map.put(index++, String.valueOf(patient.getBaselineWeight()));
			map.put(index++, String.valueOf(patient.getBaselineHeight()));
			map.put(index++, patient.getBaselineClinicStage());
			map.put(index++, patient.getBaselineFunctionalStatus());
			map.put(index++, patient.getCurrentStatus());
			map.put(index++, String.valueOf(patient.getDateCurrentStatus()));
			map.put(index++, String.valueOf(patient.getCurrentWeight()));
			map.put(index++, String.valueOf(patient.getCurrentHeight()));
			map.put(index++, String.valueOf(patient.getCurrentSystolicBP()));
			map.put(index++, String.valueOf(patient.getCurrentDiastolicBP()));
			map.put(index++, "");
			map.put(index++, patient.getFirstRegimenLine());
			map.put(index++, patient.getFirstRegimen());
			map.put(index++, patient.getCurrentRegimenLine());
			map.put(index++, patient.getCurrentRegimen());
			map.put(index++, "");
			map.put(index++, String.valueOf(patient.getDateOfLastRefill()));
			map.put(index++, String.valueOf(patient.getLastRefillDuration()));
			map.put(index++, String.valueOf(patient.getDateOfNextRefill()));
			map.put(index++, patient.getDmocType());
			map.put(index++, String.valueOf(patient.getDateDevolved()));
			map.put(index++, patient.getLastClinicStage());
			map.put(index++, String.valueOf(patient.getDateOfLastClinic()));
			map.put(index++, String.valueOf(patient.getDateOfNextClinic()));
			map.put(index++, "");
			map.put(index++, "");
			map.put(index++, "");
			map.put(index++, "");
			map.put(index, "");
			result.add(map);
		}
		return result;
	}
	
	
	public static List<Map<Integer, String>> fillRadetDataMapper(@NonNull Set<RadetDto> radetDtoSet) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (RadetDto patient : radetDtoSet) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(index++));
			map.put(index++, patient.getFacilityName());
			map.put(index++, patient.getLga());
			map.put(index++, patient.getState());
			map.put(index++, patient.getPatientId());
			map.put(index++, patient.getHospitalNum());
			map.put(index++, patient.getUniqueID());
			map.put(index++, String.valueOf(patient.getDateBirth()));
			map.put(index++, String.valueOf(patient.getAge()));
			map.put(index++, patient.getSex());
			map.put(index++, String.valueOf(patient.getArtStartDate()));
			map.put(index++, String.valueOf(patient.getCurrentWeight()));
			map.put(index++, String.valueOf(patient.getLastPickupDate()));
			map.put(index++, String.valueOf(patient.getMonthOfArvRefills()));
			
			map.put(index++, String.valueOf(patient.getIptStartDate()));
			map.put(index++, patient.getIptType());
			map.put(index++, String.valueOf(patient.getIptCompletedDate()));
			
			map.put(index++, patient.getRegimenLineAtStart());
			map.put(index++, patient.getRegimenAtStart());
			map.put(index++, patient.getCurrentRegimenLine());
			map.put(index++, patient.getCurrentRegimen());
			
			map.put(index++, String.valueOf(patient.getDateOfRegimenSwitch()));
			map.put(index++, patient.getPregnancyStatus());
			map.put(index++, String.valueOf(patient.getDateOfFullDisclosure()));
			map.put(index++, String.valueOf(patient.getDateEnrolledOnOTZ()));
			map.put(index++, String.valueOf(patient.getNumberOfSupportGroup()));
			map.put(index++, String.valueOf(patient.getNumberOfOTZModulesCompleted()));
			map.put(index++, String.valueOf(patient.getDateOfViralLoadSampleCollection()));
			map.put(index++, String.valueOf(patient.getCurrentViralLoad()));
			map.put(index++, String.valueOf(patient.getDateOfCurrentViralLoad()));
			map.put(index++, patient.getViralLoadIndication());
			map.put(index++, String.valueOf(patient.getVlResultAfterVLSampleCollection()));
			map.put(index++, String.valueOf(patient.getDateOfVLResultAfterVLSampleCollection()));
			map.put(index++, patient.getPreviousARTStatus());
			map.put(index++, String.valueOf(patient.getConfirmedDateOfPreviousARTStatus()));
			map.put(index++, String.valueOf(patient.getCurrentARTStatus()));
			map.put(index++, String.valueOf(patient.getDateOfCurrentARTStatus()));
			map.put(index++, patient.getCauseOfDead());
			map.put(index++, patient.getVACauseOfDead());
			map.put(index++, patient.getNewFacility());
			map.put(index++, patient.getArtEnrollmentSetting());
			map.put(index++, String.valueOf(patient.getDateCommencedDMOC()));
			map.put(index++, patient.getTypeOfDMOC());
			map.put(index++, String.valueOf(patient.getDateOfReturnOfDMOC()));
			map.put(index++, String.valueOf(patient.getDateOfCommencementOfEAC()));
			map.put(index++, String.valueOf(patient.getNumberOfEACSessionsCompleted()));
			map.put(index++, String.valueOf(patient.getDateOf3rdEACCompletion()));
			map.put(index++, String.valueOf(patient.getDateOfExtendedEACCompletion()));
			map.put(index++, String.valueOf(patient.getDateOfRepeatViralLoad()));
			map.put(index++, String.valueOf(patient.getDateOfCervicalCancerScreening()));
			map.put(index++, patient.getCervicalCancerScreeningType());
			map.put(index++, patient.getCervicalCancerScreeningMethod());
			map.put(index++, patient.getResultOfCervicalCancerScreening());
			map.put(index++, String.valueOf(patient.getDateOfPrecancerousLesionsTreatment()));
			map.put(index++, String.valueOf(patient.getDateReturnedToFacility()));
			map.put(index++, patient.getPrecancerousLesionsTreatmentMethods());
			map.put(index++, String.valueOf(patient.getDateBiometricsEnrolled()));
			map.put(index++, patient.getValidBiometricsEnrolled());
			map.put(index, "");
			result.add(map);
		}
		return result;
	}
	
	
}

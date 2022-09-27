package org.lamisplus.modules.report.service;

import lombok.NonNull;
import org.lamisplus.modules.report.domain.PatientLineListDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
}

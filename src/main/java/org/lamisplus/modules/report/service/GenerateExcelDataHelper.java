package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;

import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.domain.entity.Regimen;
import org.lamisplus.modules.hiv.repositories.RegimenRepository;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.domain.RadetDto;


import java.util.*;

public class GenerateExcelDataHelper {
	
	public static List<Map<Integer, String>> fillPatientLineListDataMapper(@NonNull List<PatientLineListDto> listFinalResult) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (PatientLineListDto patient : listFinalResult) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, getStringValue(String.valueOf(patient.getFacilityId())));
			map.put(index++, patient.getFacilityName());
			map.put(index++, patient.getLga());
			map.put(index++, patient.getState());
			map.put(index++, patient.getPatientId());
			map.put(index++, patient.getHospitalNum());
			map.put(index++, patient.getUniqueID());
			map.put(index++, patient.getSurname());
			map.put(index++, patient.getOtherName());
			map.put(index++, getStringValue(String.valueOf(patient.getDateBirth())));
			map.put(index++, getStringValue(String.valueOf(patient.getAge())));
			map.put(index++, patient.getSex());
			map.put(index++, patient.getMaritalStatus());
			map.put(index++, patient.getEducation());
			map.put(index++, patient.getOccupation());
			map.put(index++, patient.getStateOfResidence());
			map.put(index++, patient.getLgaOfResidence());
			map.put(index++, patient.getAddress());
			map.put(index++, patient.getPhone());
			map.put(index++, getStringValue(String.valueOf(patient.getArchived())));
			map.put(index++, patient.getCareEntryPoint());
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfConfirmedHIVTest())));
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfRegistration())));
			map.put(index++, patient.getStatusAtRegistration());
			map.put(index++, getStringValue(String.valueOf(patient.getArtStartDate())));
			map.put(index++, getStringValue(String.valueOf(patient.getBaselineCD4())));
			map.put(index++, getStringValue(String.valueOf(patient.getBaselineCDP())));
			map.put(index++, getStringValue(String.valueOf(patient.getSystolicBP())));
			map.put(index++, getStringValue(String.valueOf(patient.getDiastolicBP())));
			map.put(index++, getStringValue(String.valueOf(patient.getBaselineWeight())));
			map.put(index++, getStringValue(String.valueOf(patient.getBaselineHeight())));
			map.put(index++, patient.getBaselineClinicStage());
			map.put(index++, patient.getBaselineFunctionalStatus());
			map.put(index++, patient.getCurrentStatus());
			map.put(index++, getStringValue(String.valueOf(patient.getDateCurrentStatus())));
			map.put(index++, getStringValue(String.valueOf(patient.getCurrentWeight())));
			map.put(index++, getStringValue(String.valueOf(patient.getCurrentHeight())));
			map.put(index++, getStringValue(String.valueOf(patient.getCurrentSystolicBP())));
			map.put(index++, getStringValue(String.valueOf(patient.getCurrentDiastolicBP())));
			map.put(index++, "");
			map.put(index++, patient.getFirstRegimenLine());
			map.put(index++, patient.getFirstRegimen());
			map.put(index++, patient.getCurrentRegimenLine());
			map.put(index++, patient.getCurrentRegimen());
			map.put(index++, "");
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastRefill())));
			map.put(index++, getStringValue(String.valueOf(patient.getLastRefillDuration())));
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextRefill())));
			map.put(index++, patient.getDmocType());
			map.put(index++, getStringValue(String.valueOf(patient.getDateDevolved())));
			map.put(index++, patient.getLastClinicStage());
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfLastClinic())));
			map.put(index++, getStringValue(String.valueOf(patient.getDateOfNextClinic())));
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
			map.put(index++, getStringValue(String.valueOf(index++)));
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
		}
		return result;
	}
	
	public static List<Map<Integer, String>> fillBiometricDataMapper(@NonNull List<BiometricReportDto> biometrics) {
		
		List<Map<Integer, String>> result = new ArrayList<>();
		for (BiometricReportDto dto : biometrics) {
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, getStringValue(String.valueOf(index++)));
			map.put(index++, dto.getState());
			map.put(index++, dto.getFacilityName());
			map.put(index++, dto.getHospitalNum());
			map.put(index++, dto.getName());
			map.put(index++, getStringValue(String.valueOf(dto.getDateBirth())));
			map.put(index++, getStringValue(String.valueOf(dto.getAge())));
			map.put(index++, dto.getSex());
			map.put(index++, dto.getAddress());
			map.put(index++, getStringValue(String.valueOf(dto.getEnrollDate())));
			map.put(index++, getStringValue(String.valueOf(dto.getFingers())));
			map.put(index, dto.getValid());
			result.add(map);
		}
		return result;
	}
	
	private static String getStringValue(String value) {
		return value.replace("null", "");
	}
	
	
	public static List<Map<Integer, String>> fillPharmacyDataMapper(
			@NonNull List<ArtPharmacy> pharmacies,
			String facility, RegimenRepository repository) {
		List<Map<Integer, String>> result = new ArrayList<>();
		for (ArtPharmacy pharmacy : pharmacies) {
			StringBuilder regimenReceived = new StringBuilder();
			StringBuilder type = new StringBuilder();
			StringBuilder qty = new StringBuilder();
			JsonNode extra = pharmacy.getExtra();
			String regimens = "regimens";
			setRegimen(repository, regimenReceived, type, qty, extra, regimens);
			Map<Integer, String> map = new HashMap<>();
			int index = 0;
			map.put(index++, String.valueOf(index++));
			map.put(index++, String.valueOf(pharmacy.getFacilityId()));
			map.put(index++, facility);
			map.put(index++, pharmacy.getPerson().getUuid());
			map.put(index++, pharmacy.getPerson().getHospitalNumber());
			map.put(index++, String.valueOf(pharmacy.getVisitDate()));
			map.put(index++, type.toString());
			map.put(index++, regimenReceived.toString());
			map.put(index++, qty.toString());
			map.put(index++, pharmacy.getMmdType());
			map.put(index++, String.valueOf(pharmacy.getNextAppointment()));
			map.put(index, pharmacy.getDsdModel());
			result.add(map);
		}
		return result;
	}
	
	private static void setRegimen(
			RegimenRepository repository,
			StringBuilder regimenReceived,
			StringBuilder type,
			StringBuilder qty,
			JsonNode extra, String regimens) {
		if (extra.hasNonNull(regimens)) {
			JsonNode jsonNode = extra.get(regimens);
			for (JsonNode regimen : jsonNode) {
				if (regimen.hasNonNull("regimenId")) {
					JsonNode regimenId = regimen.get("regimenId");
					JsonNode dispenseQuantity = regimen.get("dispense");
					long id = regimenId.asLong();
					long refillQty = dispenseQuantity.asLong();
					qty.append(refillQty + ",");
					Optional<Regimen> optionalRegimen = repository.findById(id);
					optionalRegimen.ifPresent(regimen1 -> {
						String description = regimen1.getDescription();
						String regimenType = regimen1.getRegimenType().getDescription();
						regimenReceived.append(description + ",");
						type.append(regimenType + ",");
						
					});
				}
				
			}
		}
	}
	
	
}

package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.report.domain.LabReport;
import org.lamisplus.modules.report.domain.PatientLineDto;
import org.lamisplus.modules.report.domain.PharmacyReport;
import org.lamisplus.modules.report.domain.dto.ApprProjection;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
//import org.lamisplus.modules.report.utility.JsonEncryptor;
import org.lamisplus.modules.report.utility.Scrambler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;



@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateExcelDataHelper {
	List<Object> errorObjects = new ArrayList<Object>();

	private final Scrambler scrambler;
	private final SimpMessageSendingOperations messagingTemplate;
	private final ExcelService excelService;
	public static final String RESULT_OUTPUT = "Results:";
	public static final String ERROR_OUTPUT = "The error message is: ";
	public static final String RECORD_OUTPUT = "Done converting db records total size {}";

	public List<Map<Integer, Object>> fillPatientLineListDataMapper(@NonNull List<PatientLineDto> listFinalResult, Boolean scramble) {
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
                map.put(index++, getStringValue(patient.getFirstName() == null ? "" : (scramble ? scrambler.scrambleCharacters(patient.getFirstName()) : patient.getFirstName())));
                map.put(index++, getStringValue(patient.getSurname() == null ? "" : (scramble ? scrambler.scrambleCharacters(patient.getSurname()) : patient.getSurname())));
                map.put(index++, getStringValue(patient.getOtherName() == null ? "" : (scramble ? scrambler.scrambleCharacters(patient.getOtherName()) : patient.getOtherName())));
                map.put(index++, patient.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(patient.getAge())));
				map.put(index++, getStringValue(String.valueOf(patient.getSex())));
				map.put(index++, getStringValue(String.valueOf(patient.getMaritalStatus())));
				map.put(index++, getStringValue(String.valueOf(patient.getEducation())));
				map.put(index++, getStringValue(String.valueOf(patient.getOccupation())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialState())));
				map.put(index++, getStringValue(String.valueOf(patient.getResidentialLga())));
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
				map.put(index, getStringValue(patient.getCmName()));
				result.add(map);
			}
		}
		Log.info(RESULT_OUTPUT+ result.size());
		return result;
	}


	public static List<Map<Integer, Object>> fillTBLongitudinalReportDataMapper(@NonNull List<TbLongitudinalProjection> tbReportProjections) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (TbLongitudinalProjection tbReportProjection : tbReportProjections) {
			if (tbReportProjection != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getState())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getLga())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getPersonUuid())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getUniqueId())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getGender())));
				map.put(index++, tbReportProjection.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getAge())));

				map.put(index++, tbReportProjection.getDateStarted());

				// Date of last visit
				map.put(index++, tbReportProjection.getDateOfObservation());


				map.put(index++, tbReportProjection.getDateOfObservation());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbScreeningType())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbStatus())));
				map.put(index++, tbReportProjection.getCadScore());

				map.put(index++, tbReportProjection.getDateofDiagnosticTestSampleCollected());
				map.put(index++, tbReportProjection.getDateSpecimenSent());
				map.put(index++, tbReportProjection.getSpecimenType());

				map.put(index++, (tbReportProjection.getDateofTbDiagnosticResultReceived() != null) ? "Yes" : "No");

				map.put(index++, tbReportProjection.getDateDiagnosticEvaluation());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticTestType())));
				map.put(index++, tbReportProjection.getDateofTbDiagnosticResultReceived());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticResult())));
				map.put(index++, tbReportProjection.getDateTbScoreCad());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getResultTbScoreCad())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getClinicallyEvaulated())));
				map.put(index++, tbReportProjection.getDateOfChestXrayResultTestDone());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getChestXrayResult())));


				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbType())));
				map.put(index++, tbReportProjection.getTbTreatmentStartDate());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTreatmentOutcome())));
				map.put(index++, tbReportProjection.getTbCompletionDate());

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getEligibleForTPT())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getContractionForTpt())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getContractionOptions())));

				map.put(index++, tbReportProjection.getDateOfIptStart());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getRegimenName())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getIptCompletionStatus())));
				map.put(index++, tbReportProjection.getIptCompletionDate());
				map.put(index, tbReportProjection.getWeightAt());

				result.add(map);
			}
		}
		Log.info(RESULT_OUTPUT + result.size());
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

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getUniqueId())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getGender())));
				map.put(index++, tbReportProjection.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getAge())));

				map.put(index++, tbReportProjection.getArtStartDate());

				// Date of last visit
				map.put(index++, tbReportProjection.getDateOfTbScreened());


				map.put(index++, tbReportProjection.getDateOfTbScreened());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbScreeningType())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbStatus())));
				map.put(index++, tbReportProjection.getCadScore());

				map.put(index++, tbReportProjection.getDateOfTbSampleCollection());
				map.put(index++, tbReportProjection.getSpecimenSentDate());
				map.put(index++, tbReportProjection.getSpecimenType());

				map.put(index++, (tbReportProjection.getDateOfTbDiagnosticResultReceived() != null) ? "Yes" : "No");

				map.put(index++, tbReportProjection.getDateOfTbDiagnosticResultReceived());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticTestType())));
				map.put(index++, tbReportProjection.getDateOfTbDiagnosticResultReceived());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbDiagnosticResult())));
				map.put(index++, tbReportProjection.getDateTbScoreCad());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getResultTbScoreCad())));

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getClinicallyEvaulated())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getDateOfChestXrayResultTestDone())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getChestXrayResultTest())));


				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentType())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentStartDate())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getTbTreatmentOutcome())));
				map.put(index++, tbReportProjection.getTbTreatmentCompletionDate());

				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getEligibleForTPT())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getContractionForTpt())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getContractionOptions())));

				map.put(index++, tbReportProjection.getDateOfIptStart());
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getRegimenName())));
				map.put(index++, getStringValue(String.valueOf(tbReportProjection.getIptCompletionStatus())));
				map.put(index++, tbReportProjection.getIptCompletionDate());
				map.put(index, tbReportProjection.getWeightAtStartTpt());

				result.add(map);
			}
		}
		Log.info(RESULT_OUTPUT + result.size());
		return result;
	}

	public static List<Map<Integer, Object>> fillEACReportDataMapper(@NonNull List<EACReportProjection> eacReportProjections) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (EACReportProjection eacReportProjection : eacReportProjections) {
			if (eacReportProjection != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;

				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getState())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getLga())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getLgaOfResidence())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getPatientId())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getUniqueId())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getSex())));
				map.put(index++, eacReportProjection.getDateOfBirth());
				map.put(index++, eacReportProjection.getArtStartDate());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getRegimenAtArtStart())));
				map.put(index++, eacReportProjection.getDateOfStartOfRegimenBeforeUnsuppressedVLR());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getRegimenLineBeforeUnsuppression())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getRegimenBeforeUnsuppression())));
				map.put(index++, eacReportProjection.getLastPickupDateBeforeUnsuppressedVLR());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMonthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR())));

				map.put(index++, eacReportProjection.getDateOfVLSCOfUnsuppressedVLR());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMostRecentUnsuppressedVLR())));
				map.put(index++, eacReportProjection.getDateOfUnsuppressedVLR());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getUnsuppressedVLRIndication())));

				map.put(index++, eacReportProjection.getDateOfCommencementOfFirstEAC());
				map.put(index++, eacReportProjection.getDateOfCommencementOfSecondEAC());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMethodOfFirstEACSession())));

				map.put(index++, eacReportProjection.getDateOfCommencementOfSecondEAC());
				map.put(index++, eacReportProjection.getDateOfCommencementOfThirdEAC());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMethodOfSecondEACSession())));

				map.put(index++, eacReportProjection.getDateOfCommencementOfThirdEAC());
				map.put(index++, eacReportProjection.getDateOfCommencementOfFourthEAC());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMethodOfThirdEACSession())));

				map.put(index++, eacReportProjection.getDateOfCommencementOfFourthEAC());
				map.put(index++, eacReportProjection.getDateOfCommencementOfFifthEAC());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMethodOfFourthEACSession())));

				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getNumberOfEACSessionsCompleted())));
				map.put(index++, eacReportProjection.getDateOfRepeatViralLoadPostEACSampleCollected());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getRepeatViralLoadResultPostEAC())));
				map.put(index++, eacReportProjection.getDateOfRepeatViralLoadResultPostEACVL());

				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getEligibleForSwitch()))); // eligble for switch
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getReferredToSwitchCommittee())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getReferredToSwitchCommitteePostSwitchEAC())));
				map.put(index++, eacReportProjection.getDateReferredToSwitchCommittedPostSwitchEAC());

				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getMethodOfPostSwitchEACSession())));

				map.put(index++, eacReportProjection.getDateOfRepeatViralLoadPostSwitchEACSampleCollected());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getRepeatViralLoadResultPostSwitchEAC())));
				map.put(index++, eacReportProjection.getDateOfRepeatViralLoadResultPostSwitchEACVL());

				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getEligibleForSwitchPostSwitchEAC())));
				map.put(index++, eacReportProjection.getDateSwitched());

				map.put(index++, eacReportProjection.getStartDateOfSwitchedRegimen());
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getSwitchedARTRegimenLine())));
				map.put(index++, getStringValue(String.valueOf(eacReportProjection.getSwitchedARTRegimen())));

				map.put(index, getStringValue(String.valueOf(eacReportProjection.getCaseManager())));

				result.add(map);
			}
		}
		Log.info(RESULT_OUTPUT + result.size());
		return result;
	}


	public static List<Map<Integer, Object>> fillNCDReportDataMapper(@NonNull List<NCDReportProjection> ncdReportProjections) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (NCDReportProjection ncdReportProjection : ncdReportProjections) {
			if (ncdReportProjection != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getState())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getLga())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getDatimId())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getFacilityName())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getPatientId())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getHospitalNumber())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getUniqueId())));
				map.put(index++, ncdReportProjection.getDateOfBirth());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getAge())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getSex())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getMaritalStatus())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getEducation())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getOccupation())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getStateOfResidence())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getLgaOfResidence())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getPregnancyStatus())));
				map.put(index++, ncdReportProjection.getArtStartDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getRegimenLineAtStart())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getReasonsLtfuIit())));

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentArtRegimen())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentRegimenLine())));

				map.put(index++, ncdReportProjection.getDateRegimenSwitch());
				map.put(index++, ncdReportProjection.getLastPickUpDate());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getMonthsOfArvRefill())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getStatus())));
				map.put(index++, ncdReportProjection.getStatusDate());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getEnrollmentSetting())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getDsdModel())));

				map.put(index++, ncdReportProjection.getDatePrevHypertensive());
				map.put(index++, ncdReportProjection.getDateNewlyHypertensive());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineWaistCircumference())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineWeight())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineHeight())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineBmi())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineSystolic())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineDiastolic())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineFastingBloodSugar())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineRandomBloodSugar())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineBloodTotalCholesterol())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineHdl())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineLdl())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineSodium())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselinePotassium())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineUrea())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBaselineCreatinine())));

				map.put(index++, ncdReportProjection.getHtnStartDate());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getHtnStartRegimen())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentHtnRegimen())));
				map.put(index++, ncdReportProjection.getLastHtnPickUpDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getMonthsOfHTNRefill())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentHtnStatus())));
				map.put(index++, ncdReportProjection.getDateCurrentHtnStatus());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getReasonsLtfuIit())));
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getBodyWeight())));
				map.put(index++, ncdReportProjection.getCurrentWeightDate());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getHeight())));
				map.put(index++, ncdReportProjection.getCurrentHeightDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentBmi())));
				map.put(index++, ncdReportProjection.getCurrentBmiDate());

				map.put(index++, ncdReportProjection.getCurrentWaistCircumferenceDate());
				map.put(index++, ncdReportProjection.getCurrentWaistCircumferenceDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getWaistHipRatio())));
				map.put(index++, ncdReportProjection.getWaistHipRatioDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getSystolic())));
				map.put(index++, ncdReportProjection.getCurrentSystolicDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getDiastolic())));
				map.put(index++, ncdReportProjection.getCurrentDiastolicDate());

				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentFastingBloodsSugar())));
				map.put(index++, ncdReportProjection.getDateCurrentFastingBloodSugar());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentRandomBloodSugar())));
				map.put(index++, ncdReportProjection.getDateCurrentRandomBloodSugar());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentBloodTotalCholesterol())));
				map.put(index++, ncdReportProjection.getDateCurrentBloodTotalCholesterol());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentHdl())));
				map.put(index++, ncdReportProjection.getDateCurrentHdl());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentLdl())));
				map.put(index++, ncdReportProjection.getDateCurrentLdl());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentSodium())));
				map.put(index++, ncdReportProjection.getDateCurrentSodium());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentPotassium())));
				map.put(index++, ncdReportProjection.getDateCurrentPotassium());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentUrea())));
				map.put(index++, ncdReportProjection.getDateCurrentUrea());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentCreatinine())));
				map.put(index++, ncdReportProjection.getDateCurrentCreatinine());

				map.put(index++, ncdReportProjection.getDateOfViralLoadSample());
				map.put(index++, getStringValue(String.valueOf(ncdReportProjection.getCurrentViralLoad())));
				map.put(index++, ncdReportProjection.getDateOfCurrentViralLoad());
				map.put(index, getStringValue(String.valueOf(ncdReportProjection.getViralLoadIndication())));

				result.add(map);
			}
		}
		Log.info(RESULT_OUTPUT+ result.size());
		return result;
	}

	public  List<Map<Integer, Object>> fillRadetDataMapper(@NonNull List<RADETDTOProjection> reportDtos) {
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
//				boolean isCurrentVlValid = isValidResult(radetReportDto.getCurrentViralLoad());
//				currentVl = isCurrentVlValid ? Double.parseDouble(radetReportDto.getCurrentViralLoad()) : null;
//				boolean isRepeatValidNumber = isValidResult(radetReportDto.getRepeatViralLoadResult());
//				repeatVl = isRepeatValidNumber ? Double.parseDouble(radetReportDto.getRepeatViralLoadResult()) : null;
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
//				map.put(index++, radetReportDto.getTargetGroup());
				map.put(index++, radetReportDto.getCurrentWeight());
				map.put(index++, radetReportDto.getPregnancyStatus());
				map.put(index++, radetReportDto.getDateOfBirth());

				map.put(index++, radetReportDto.getAge());
				map.put(index++, getStringValue(String.valueOf(radetReportDto.getCareEntry())));
				map.put(index++, radetReportDto.getDateOfRegistration());
				map.put(index++, radetReportDto.getArtStartDate());
				map.put(index++, radetReportDto.getDateEnrolled());
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
				map.put(index++, radetReportDto.getCurrentViralLoad());
				map.put(index++, radetReportDto.getDateOfCurrentViralLoad());
				map.put(index++, radetReportDto.getViralLoadIndication());
				map.put(index++, radetReportDto.getVlEligibilityStatus());
				map.put(index++, radetReportDto.getDateOfVlEligibilityStatus());

				//current status
				map.put(index++, currentStatus);
				map.put(index++, radetReportDto.getCurrentStatusDate());
				map.put(index++, radetReportDto.getClientVerificationOutCome());

				map.put(index++, radetReportDto.getCauseOfDeath());
				map.put(index++, radetReportDto.getVaCauseOfDeath());

				//previous status
				map.put(index++, previousStatus);
				map.put(index++, radetReportDto.getPreviousStatusDate());


				map.put(index++, radetReportDto.getEnrollmentSetting());
				//TB
				map.put(index++, radetReportDto.getDateOfTbScreened());
				map.put(index++, radetReportDto.getTbScreeningType());
				map.put(index++, radetReportDto.getCadScore());
				map.put(index++, radetReportDto.getTbStatus());
				//tb lab
				map.put(index++, radetReportDto.getDateOfTbSampleCollected());
				map.put(index++, radetReportDto.getTbDiagnosticTestType());
				map.put(index++, radetReportDto.getDateofTbDiagnosticResultReceived());
				map.put(index++, radetReportDto.getTbDiagnosticResult());

				map.put(index++, radetReportDto.getDateTbScoreCad());
				map.put(index++, radetReportDto.getResultTbScoreCad());


				map.put(index++, radetReportDto.getTbTreatmentStartDate());
				map.put(index++, radetReportDto.getTbTreatementType());
				map.put(index++, radetReportDto.getTbCompletionDate());
				map.put(index++, radetReportDto.getTbTreatmentOutcome());

				//TPT
                map.put(index++, radetReportDto.getEligibilityTpt());
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
				map.put(index++, radetReportDto.getDsdOutlet());
				map.put(index++, radetReportDto.getDateReturnToSite());

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


				//biometrics
				map.put(index++, radetReportDto.getDateBiometricsEnrolled());
				map.put(index++, radetReportDto.getNumberOfFingersCaptured());
				map.put(index++, radetReportDto.getDateBiometricsRecaptured());
				map.put(index++, radetReportDto.getNumberOfFingersRecaptured());

				//case manager
				map.put(index, radetReportDto.getCaseManager());

				// File versioning
//				map.put(index, encryptData(String.valueOf(currentRecord)));


				result.add(map);
				sn++;
			} catch (Exception e) {
				LOG.error("An error occurred when converting db record to excel for patient id {}", currentRecord.getPersonUuid());
				writeToErrorFile(currentRecord);
				LOG.error(ERROR_OUTPUT + e.getMessage());
			}
		}
		LOG.info(RECORD_OUTPUT + result.size());
		return result;
	}



    public  List<Map<Integer, Object>> fillApprRadetDataMapper(@NonNull List<ApprRADETProjection> reportDtos) {
        List<Map<Integer, Object>> result = new ArrayList<>();

        int sn = 1;
        Log.info("converting RADET db records to excel ....");
        ApprRADETProjection currentRecord = null;
        for (ApprRADETProjection radetReportDto : reportDtos) {
            try {
                currentRecord = radetReportDto;
                Map<Integer, Object> map = new HashMap<>();

                int index = 0;

                map.put(index++, sn);
                map.put(index++, radetReportDto.getPeriod());
                map.put(index++, radetReportDto.getIpname());
                map.put(index++, radetReportDto.getState());
                map.put(index++, radetReportDto.getLga());
//                map.put(index++, radetReportDto.getLgaOfResidence());
                map.put(index++, radetReportDto.getFacilityname());
                map.put(index++, radetReportDto.getDatimid());
                map.put(index++, radetReportDto.getPersonuuid());
//                map.put(index++, radetReportDto.getNdrPatientIdentifier());
                map.put(index++, radetReportDto.getHospitalnumber());
                map.put(index++, radetReportDto.getUniqueid());
                //ovc
                map.put(index++, radetReportDto.getHouseholduniqueno());
                map.put(index++, radetReportDto.getOvcuniqueid());

                map.put(index++, radetReportDto.getGender());
                map.put(index++, radetReportDto.getTargetgroup());
                map.put(index++, radetReportDto.getCurrentweight());
                map.put(index++, radetReportDto.getPregnancystatus());
                map.put(index++, radetReportDto.getDateofbirth());

                map.put(index++, radetReportDto.getAge());
                map.put(index++, getStringValue(String.valueOf(radetReportDto.getCareentry())));
                map.put(index++, radetReportDto.getDateofregistration());
                map.put(index++, radetReportDto.getDateofenrollment());
                map.put(index++, radetReportDto.getArtstartdate());
                map.put(index++, radetReportDto.getLastpickupdate());
                map.put(index++, radetReportDto.getMonthsofarvrefill());

                map.put(index++, radetReportDto.getRegimenlineatstart());
                map.put(index++, radetReportDto.getRegimenatstart());
                map.put(index++, radetReportDto.getDateofcurrentregimen());
                map.put(index++, radetReportDto.getCurrentregimenline());
                map.put(index++, radetReportDto.getCurrentartregimen());

                //cd4
                map.put(index++, radetReportDto.getCurrentclinicalstage());
                map.put(index++, radetReportDto.getDateoflastcd4count());
                map.put(index++, radetReportDto.getLastcd4count());
                //vl
                map.put(index++, radetReportDto.getDateofviralloadsamplecollection());
                map.put(index++, radetReportDto.getDateofcurrentviralloadsample());
                map.put(index++, radetReportDto.getCurrentviralload());
                map.put(index++, radetReportDto.getDateofcurrentviralload());
                map.put(index++, radetReportDto.getViralloadindication());
                map.put(index++, radetReportDto.getVleligibilitystatus());
                map.put(index++, radetReportDto.getDateofvleligibilitystatus());

                //current status
                map.put(index++, radetReportDto.getCurrentstatus());
                map.put(index++, radetReportDto.getCurrentstatusdate());
                map.put(index++, radetReportDto.getClientverificationoutcome());

                map.put(index++, radetReportDto.getCauseofdeath());
                map.put(index++, radetReportDto.getVacauseofdeath());

                //previous status
                map.put(index++, radetReportDto.getPreviousstatus());
                map.put(index++, radetReportDto.getPreviousstatusdate());


                map.put(index++, radetReportDto.getEnrollmentsetting());
                //TB
                map.put(index++, radetReportDto.getDateodtbscreened());
                map.put(index++, radetReportDto.getTbscreeningtype());
                map.put(index++, radetReportDto.getCadscore());
                map.put(index++, radetReportDto.getTbstatus());
                //tb lab
                map.put(index++, radetReportDto.getDateoftbsamplecollected());
                map.put(index++, radetReportDto.getTbdiagnostictesttype());
                map.put(index++, radetReportDto.getDateoftbdiagnosticresultreceived());
                map.put(index++, radetReportDto.getTbdiagnosticresult());

                map.put(index++, radetReportDto.getDatetbscorecad());
                map.put(index++, null);


                map.put(index++, radetReportDto.getTbtreatmentstartdate());
                map.put(index++, radetReportDto.getTbtreatmenttype());
                map.put(index++, radetReportDto.getTbcompletiondate());
                map.put(index++, radetReportDto.getTbtreatmentoutcome());

                //TPT
                map.put(index++, null);
                map.put(index++, radetReportDto.getDateofiptstart());
                map.put(index++, radetReportDto.getIpttype());
                map.put(index++, radetReportDto.getIptcompletiondate());
                map.put(index++, radetReportDto.getIptcompletionstatus());

                //EAC
                map.put(index++, radetReportDto.getDateofcommencementofeac());
                map.put(index++, radetReportDto.getNumberofeacsessioncompleted());
                map.put(index++, radetReportDto.getDateoflasteacsessioncompleted());
                map.put(index++, radetReportDto.getDateofextendeaccompletion());
                map.put(index++, radetReportDto.getDateofrepeatviralloadeacsamplecollection());
                map.put(index++, radetReportDto.getRepeatviralloadresult());
                map.put(index++, radetReportDto.getDateofrepeatviralloadresult());

                //DSD MOdel
                map.put(index++, radetReportDto.getDateofdevolvement());
                map.put(index++, radetReportDto.getModeldevolveto());
                map.put(index++, radetReportDto.getDateofcurrentdsd());
                map.put(index++, radetReportDto.getCurrentdsdmodel());
                map.put(index++, radetReportDto.getDsdoutlet());
                map.put(index++, radetReportDto.getDatereturntosite());

                //chronic care
                map.put(index++, null);
                map.put(index++, null);

                //cervicalCancerScreeningType
                map.put(index++, radetReportDto.getDateofcervicalcancerscreening());
                map.put(index++, radetReportDto.getCervicalcancerscreeningtype());
                map.put(index++, radetReportDto.getCervicalcancerscreeningmethod());
                map.put(index++, radetReportDto.getResultofcervicalcancerscreening());
                //Precancerous
                map.put(index++, radetReportDto.getTreatmentmethoddate());
                map.put(index++, radetReportDto.getCervicalcancertreatmentscreened());


                //biometrics
                map.put(index++, radetReportDto.getDatebiometricenrolled());
                map.put(index++, radetReportDto.getNumberoffingerscaptured());
                map.put(index++, radetReportDto.getDatebiometricsrecaptured());
                map.put(index++, radetReportDto.getNumberoffingersrecaptured());

                //case manager
                map.put(index, radetReportDto.getCasemanager());

                // File versioning
//				map.put(index, encryptData(String.valueOf(currentRecord)));


                result.add(map);
                sn++;
            } catch (Exception e) {
                LOG.error("An error occurred when converting db record to excel for patient id {}", radetReportDto.getPersonuuid());
                writeToErrorFile(currentRecord);
                LOG.error(ERROR_OUTPUT + e.getMessage());
            }
        }
        LOG.info(RECORD_OUTPUT + result.size());
        return result;
    }

//	private Object encryptData (String obj) {
//		try {
//			JsonEncryptor server = new JsonEncryptor();
//			server.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
//			String encryptedMessage = server.encrypt(obj);
//			System.err.println("Encrypted Message : " + encryptedMessage);
//		} catch (Exception ignored) {
//		}
//		return null;
//	}



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

	public  List<Map<Integer, Object>> fillPmtctHtsDataMapper(@NonNull List<PmtctDto> pmtctHtsReportDtos, String reportType) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting HTS db records to excel ....");
		try {
			for (PmtctDto pmtctDto : pmtctHtsReportDtos) {
				if (pmtctDto != null) {
					Map<Integer, Object> map = new HashMap<>();
					int index = 0;
					map.put(index++, String.valueOf(sn));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getState())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getLga())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getFacilityName())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPersonUuid())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHospitalNumber())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getAge())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getMaritalStatus())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getSetting())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getFacilitySetting())));
					map.put(index++, reportType.equals("GoN")  ? getStringValue(String.valueOf(pmtctDto.getGonModalities())) : getStringValue(String.valueOf(pmtctDto.getPepfarModalities())));
					map.put(index++, pmtctDto.getDate());
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getGravida())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getParity())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPreviouslyKnownHivPositive())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getTypeOfHivTest())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHivEarlyDetect())));
					map.put(index++, null);// Suspected Acute Infection
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHivEarlyDetectViralLoad())));
					map.put(index++, pmtctDto.getDateOfVisit());
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getFinalHivTestResult())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getKnownPositive())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHbTestResult())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHbTreatment())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getDateTestedForHepatitisC())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getHepatitisC())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getSyphilisTestResult())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getSyphilisTreatment())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getDateOfMaternalRestesting())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getMaternalRestesting())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getTbScreeningStatus())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getTbReferred())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPartnerTestedHiv())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPartnerTestedSyphilis())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPartnerTestedHb())));
					map.put(index++, getStringValue(String.valueOf(pmtctDto.getPartnerReferal())));

					result.add(map);
					sn++;
				}
			}
			LOG.info(RECORD_OUTPUT, result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error(ERROR_OUTPUT + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}


	public List<Map<Integer, Object>> fillHtsDataMapper(@NonNull List<HtsReportDto> htsReportDtos, String reportType, Boolean scramble) {
		List<Map<Integer, Object>> result = new ArrayList<>();
		int sn = 1;
		Log.info("converting HTS db records to excel ....");
		try {
			for (HtsReportDto htsReportDto : htsReportDtos) {
				if (htsReportDto != null) {
					Map<Integer, Object> map = new HashMap<>();
					int index = 0;
					map.put(index++, String.valueOf(sn));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getDatimId())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFacilityName())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getClientCode())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPersonUuid())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFirstName() != null ? (scramble ? scrambler.scrambleCharacters(htsReportDto.getFirstName()) : htsReportDto.getFirstName()) : "" )));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSurname() != null ? (scramble ? scrambler.scrambleCharacters(htsReportDto.getSurname()) : htsReportDto.getSurname()) : "" )));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getOtherName() != null ? (scramble ? scrambler.scrambleCharacters(htsReportDto.getOtherName()) : htsReportDto.getOtherName()) : "")));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSex())));
					map.put(index++, htsReportDto.getAge());
					map.put(index++, htsReportDto.getDateOfBirth());
                    map.put(index++, getStringValue(String.valueOf(htsReportDto.getPhoneNumber() != null ? (scramble ? scrambler.scrambleCharacters(htsReportDto.getPhoneNumber()) : htsReportDto.getPhoneNumber()) : "")));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getMaritalStatus())));
                    map.put(index++, getStringValue(String.valueOf(htsReportDto.getAddress() != null ? (scramble ? scrambler.scrambleCharacters(htsReportDto.getAddress()).replace("\"", "") : htsReportDto.getAddress()) : "" )));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getLgaOfResidence())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getStateOfResidence())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getEducationStatus())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getOccupation())));
					map.put(index++, htsReportDto.getDateOfVisit());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFirstTimeVisit())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getEntryPoint())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPreviouslyTestedNegative())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getTestingSetting())));

					map.put(index++, reportType.equals("GoN")  ? getStringValue(String.valueOf(htsReportDto.getGonModalities())) : getStringValue(String.valueOf(htsReportDto.getPepfarModalities())));

					map.put(index++, getStringValue(String.valueOf(htsReportDto.getTypeOfSession())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPregnancyStatus())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIndexTesting())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIndexClientCode())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getIndexRelationship())));
					map.put(index++, htsReportDto.getDateOfPreviousHts());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPreviousHtsResult())));
					map.put(index++, htsReportDto.getNumberOfCounts());
					map.put(index++, htsReportDto.getDateOfVisit());
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getTypeOfHivTestDone())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getFinalHivTestResult())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSuspected())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPositiveUuid())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getRecencyTest())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getSyphilisTestResult())));
                    map.put(index++, getStringValue(String.valueOf(htsReportDto.getSource())));

					map.put(index++, null); //Risk Score
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCompletedBy()))); //Tester Code
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getPreviouslyTestedThisYear())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getHivTestKitsProvided())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCategoryOfClients())));
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getCondomsProvided())));
                    map.put(index++, null); //ML Score
                    map.put(index++, null); //Ml Status
					map.put(index++, getStringValue(String.valueOf(htsReportDto.getLatitude())));
					map.put(index, getStringValue(String.valueOf(htsReportDto.getLongitude())));


					result.add(map);
						sn++;
					}
				}
				LOG.info(RECORD_OUTPUT, result.size());
				return result;
			}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error(ERROR_OUTPUT + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

    private String handleUuidSafely(UUID value) {
        return value == null ? "" : value.toString();
    }


	private static String getStringValue(String value) {
		return value!= null ? value.replace("null", "") : "";
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
			LOG.info(RECORD_OUTPUT + result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error(ERROR_OUTPUT + e.getMessage());
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
			LOG.info(RECORD_OUTPUT, result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error(ERROR_OUTPUT + e.getMessage());
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
			LOG.info(RECORD_OUTPUT, result.size());
			return result;
		}catch (Exception e) {
			LOG.error("An error occurred when converting db records to excel");
			LOG.error(ERROR_OUTPUT + e.getMessage());
		}
		return result;
	}

	public static List<Map<Integer, Object>> fillClientServiceListDataMapper(@NonNull List<ClientServiceDto> listFinalResult)  {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (ClientServiceDto clientService : listFinalResult) {
			if (clientService != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				try {
					map.put(index++, getStringValue(clientService.getFacilityState()));
					map.put(index++, getStringValue(clientService.getFacilityName()));
					map.put(index++, getStringValue(clientService.getSerialEnrollmentNo()));
					map.put(index++, getStringValue(clientService.getPersonUuid()));
					map.put(index++, clientService.getHospitalNumber());
					map.put(index++, getStringValue(String.valueOf(clientService.getDateOfObservation())));
					map.put(index++, getStringValue(clientService.getAnyOfTheFollowingList()));
					map.put(index++, getStringValue(clientService.getDateOfAttempt()));
					map.put(index++, getStringValue(clientService.getNoAttempts()));
					map.put(index++, getStringValue(clientService.getVerificationAttempts()));
					map.put(index++, getStringValue(clientService.getOutcome()));
					map.put(index++, getStringValue(clientService.getDsdModel()));
					map.put(index++, getStringValue(clientService.getComment()));
					map.put(index++, clientService.getReturnedToCare());
					map.put(index++, getStringValue(clientService.getReferredTo()));
					map.put(index++, getStringValue(clientService.getDiscontinuation()));
					map.put(index++, clientService.getDateOfDiscontinuation());
					map.put(index++, getStringValue(clientService.getReasonForDiscontinuation()));
					//Client Verification triggers
					map.put(index++, getStringValue(clientService.getNoInitBiometric()));
					map.put(index++, getStringValue(clientService.getDuplicatedDemographic()));
					map.put(index++, getStringValue(clientService.getLastVisitIsOver18M()));
					map.put(index++, getStringValue(clientService.getIncompleteVisitData()));
					map.put(index++, getStringValue(clientService.getNoRecapture()));
					map.put(index++, getStringValue(clientService.getLongIntervalsARVPickup()));
					map.put(index++, getStringValue(clientService.getSameSexDOBARTStartDate()));
					map.put(index++, getStringValue(clientService.getPickupByProxy()));
					map.put(index++, getStringValue(clientService.getRepeatEncounterNoPrint()));
					map.put(index++, getStringValue(clientService.getOtherSpecifyForCV()));

					result.add(map);
				} catch (Exception e) {
					try (FileWriter writer = new FileWriter(System.getProperty("user.home") + "/Downloads/Client_Verification_Report_error_log_" + LocalDate.now() +".txt" , true)) {
						writer.write("ID: " + clientService.getHospitalNumber() + "\n");
						writer.write("Error Message: " + e.getMessage() + "\n");
						writer.write("Client Verification Row: " + new ObjectMapper().writeValueAsString(clientService) + "\n");
						writer.write("-------------------------------\n");

					} catch (IOException io) {
						Log.error("Error writing to log file", io);
					}
				}
			}
		}
		Log.info(RESULT_OUTPUT + result.size());
		return result;
	}


	public static List<Map<Integer, Object>> fillFamilyIndexMapper(@NonNull List<FamilyIndexReportDtoProjection> listFinalResult)  {
		List<Map<Integer, Object>> result = new ArrayList<>();
		for (FamilyIndexReportDtoProjection familyIndex : listFinalResult) {
			if (familyIndex != null) {
				Map<Integer, Object> map = new HashMap<>();
				int index = 0;
				try {
					map.put(index++, getStringValue(familyIndex.getState()));
					map.put(index++, getStringValue(familyIndex.getLga()));
					map.put(index++, getStringValue(familyIndex.getFacilityName()));
					map.put(index++, getStringValue(familyIndex.getDatimId()));
					map.put(index++, getStringValue(familyIndex.getSurname()));
					map.put(index++, getStringValue(familyIndex.getFirstName()));
					map.put(index++, getStringValue(familyIndex.getClientCode()));
                    map.put(index++, getStringValue(familyIndex.getEntryPoint()));
					map.put(index++, getStringValue(familyIndex.getClientCategory()));
					map.put(index++, familyIndex.getDateOfService());
					map.put(index++, getStringValue(String.valueOf(familyIndex.getOfferedPns())));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getAcceptedPns())));
					map.put(index++, familyIndex.getDateOfService());
					map.put(index++, getStringValue(String.valueOf(familyIndex.getContactCode())));
					map.put(index++, getStringValue(familyIndex.getNameOfIndexClient()));
					map.put(index++, getStringValue(familyIndex.getRelationshipToIndex()));
					map.put(index++, getStringValue(familyIndex.getSex()));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getAge())));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getPhone())));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getAddress())));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getNotificationMethod())));
					map.put(index++, getStringValue(String.valueOf(familyIndex.getAttempts())));
                    map.put(index++, getStringValue(String.valueOf(familyIndex.getKnownHivPositive())));
                    map.put(index++, familyIndex.getDateTestedHiv());
                    map.put(index++, getStringValue(String.valueOf(familyIndex.getHivTestResult())));
                    map.put(index++, familyIndex.getDateEnrolledArt());
                    map.put(index++, getStringValue(String.valueOf(familyIndex.getUan())));
                    map.put(index++, familyIndex.getDateEnrolledOvc());
					map.put(index, getStringValue(String.valueOf(familyIndex.getOvcId())));

				} catch (Exception e) {
					LOG.error("An error occurred when converting db records to excel");
					LOG.error(ERROR_OUTPUT + e.getMessage());
				}
				result.add(map);

			}
		}
		Log.info(RESULT_OUTPUT + result.size());
		return result;
	}

    public List<Map<Integer, Object>> fillApprDataMapper(List<ApprProjection> apprDto) {

        List<Map<Integer, Object>> result = new ArrayList<>();
        for (ApprProjection appr : apprDto) {
            if (appr != null) {
                Map<Integer, Object> map = new HashMap<>();
                int index = 0;
                try {
                    map.put(index++, getStringValue(appr.getOrgUnit()));
                    map.put(index++, getStringValue(appr.getPeriod()));
                    map.put(index++, getStringValue(appr.getDataElement()));
                    map.put(index++, getStringValue(appr.getData_element_name()));
                    map.put(index++, getStringValue(appr.getCategoryOptionCombo()));

                    map.put(index++, getStringValue(appr.getCategoryOptionComboName()));
                    map.put(index++, null);
                    map.put(index++, getStringValue(String.valueOf(appr.getValue())));
                    map.put(index++, getStringValue(appr.getFacilityname()));
                    map.put(index++, getStringValue(appr.getState()));
                    map.put(index++, getStringValue(appr.getLga()));
                    map.put(index, getStringValue(appr.getIpname()));

                } catch (Exception e) {
                    LOG.error("An error occurred when converting db records to excel");
                    LOG.error(ERROR_OUTPUT + e.getMessage());
                }
                result.add(map);

            }
        }
        Log.info(RESULT_OUTPUT + result.size());
        return result;
    }
}

package org.lamisplus.modules.report.service;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
	// EXTENSION
	public static final String EXCEL_EXTENSION_XLSX = ".xlsx";
	public static final String EXCEL_EXTENSION_XLS = ".xls";
	
	// Sheet name
	public static final String PATIENT_LINE_LIST = "patient_line__list";
	public static final String RADET_SHEET = "radet";
	
	// headers
	public static final List<String> PATIENT_LINE_LIST_HEADER = Arrays.asList(
			"Datim Id",
			"Facility Name",
			"LGA",
			"State",
			"Patient Id",
			"Hospital Num",
			"Unique ID",
			"Surname",
			"Other Name",
			"Date Birth",
			"Age",
			"Gender",
			"Marital Status",
			"Education",
			"Occupation",
			"State of Residence",
			"Lga of Residence",
			"Address",
			"Phone",
			"Archived",
			"Care Entry Point",
			"Date of Confirmed HIV Test (yyyy-mm-dd)",
			"Date Registration (yyyy-mm-dd)",
			"Status at Registration",
			"ART Start Date (yyyy-mm-dd)",
			"Baseline CD4",
			"Baseline CDP",
			"Systolic BP",
			"Diastolic BP",
			"Baseline Weight (kg)",
			"Baseline Height (cm)",
			"Baseline Clinic Stage",
			"Baseline Functional Status",
			"Current Status",
			"Date Current Status (yyyy-mm-dd)",
			"Current Weight (kg)",
			"Current Height (cm)",
			"Current Systolic BP",
			"Current Diastolic BP",
			"Adherence",
			// "Waist Circumference(cm)",
			"First Regimen Line",
			"First Regimen",
			// "First NRTI",
			// "First NNRTI",
			"Current Regimen Line",
			"Current Regimen",
			// "Current NRTI",
			// "Current NNRTI",
			"Date Substituted/Switched (yyyy-mm-dd)",
			"Date of Last Refill",
			"Last Refill Duration (days)",
			"Date of Next Refill (yyyy-mm-dd)",
			"DMOC Type",
			"Date Devolved (yyyy-mm-dd)",
			"Last Clinic Stage",
			"Date of Last Clinic (yyyy-mm-dd)",
			"Date of Next Clinic (yyyy-mm-dd)",
			// "Last CD4",
			// "Last CD4p",
			// "Date of Last CD4 (yyyy-mm-dd)",
			//"Last Visitec CD4",
			//"Date of Last Visitec CD4 (yyyy-mm-dd)",
			//"Last TB-LAM",
			//"Date of Last TB-LAM (yyyy-mm-dd)",
			// "Last Cryptococcal Antigen",
			//"Date of Last Cryptococcal Antigen (yyyy-mm-dd)",
			"Date of Sample Collection",
			"Last Viral Load",
			"Date of Last Viral Load (yyyy-mm-dd)",
			"Viral Load Indication",
			// "Date Returned to Facility (yyyy-mm-dd)",
			// "Co-morbidities",
			"Case-manager"
	);
	
	// [10 * 59] = 590  just using a simple thread pool
	public static final List<String> RADET_HEADER =
			Arrays.asList(
					"S/No.",
					"State",
					"LGA",
					"Facility Name",
					"Patient Id",
					"Hospital Num",
					"Unique ID",
					"Date Birth (yyyy-mm-dd)",
					"Age",
					"Sex",
					"ART Start Date (yyyy-mm-dd)",
					"Current Weight (kg)",
					"Last Pickup Date (yyyy-mm-dd)",
					"Months of ARV Refill",
					"Date of TPT Start (yyyy-mm-dd)",
					"TPT Type",
					"TPT Completion date (yyyy-mm-dd)",
					"Regimen Line at ART Start",
					"Regimen at ART Start",
					"Current Regimen Line",
					"Current ART Regimen",
					"Date of Regimen Switch/ Substitution",
					"Pregnancy Status",
					"Date of Full Disclosure (yyyy-mm-dd)",
					"Date Enrolled on OTZ (yyyy-mm-dd)",
					"Number of Support Group (OTZ Club) meeting attended",
					"Number of OTZ Modules completed",
					"Date of Viral Load Sample Collection (yyyy-mm-dd)",
					"Current Viral Load (c/ml)",
					"Date of Current Viral Load (yyyy-mm-dd)",
					"Viral Load Indication",
					"VL Result After VL Sample Collection (c/ml)",
					"Date of VL Result After VL Sample Collection (yyyy-mm-dd)",
					"Previous ART Status",
					"Confirmed Date of Previous ART Status",
					"Current ART Status",
					"Date of Current ART Status",
					//"RTT",
					"If Dead, Cause of Dead",
					"VA Cause of Dead",
					"If Transferred out, new Facility",
					"ART Enrollment Setting",
					"Date Commenced DMOC (yyyy-mm-dd)",
					"Type of DMOC",
					"Date of Return of DMOC Client to Facility (yyyy-mm-dd)",
					"Date of Commencement of EAC (yyyy-mm-dd)",
					"Number of EAC Sessions Completed",
					"Date of 3rd EAC Completion (yyyy-mm-dd)",
					"Date of Extended EAC Completion (yyyy-mm-dd)",
					"Date of Repeat Viral Load - Post EAC VL Sample Collected (yyyy-mm-dd)",
					//"Co-morbidities",
					"Date of Cervical Cancer Screening (yyyy-mm-dd)",
					"Cervical Cancer Screening Type",
					"Cervical Cancer Screening Method",
					"Result of Cervical Cancer Screening",
					"Date of Precancerous Lesions Treatment (yyyy-mm-dd)",
					"Date Returned to Facility (yyyy-mm-dd)",
					"Precancerous Lesions Treatment Methods",
					"Date Biometrics Enrolled (yyyy-mm-dd)",
					"Valid Biometrics Enrolled?",
					"Case-manager"
			);
	
	public static final String PHARMACY_SHEET = "pharmacy-report";
	public static final String BIOMETRIC_SHEET_SHEET = "biometric-report";
	public static final String LAB_SHEET_NAME = "laboratory-report";
	public static final List<String> PHARMACY_HEADER =
			Arrays.asList(
					"S/No",
					"Facility Name",
					"DATIM Id",
					"Patient Id",
					"Hospital Num",
					"Date Visit(yyyy-mm-dd)",
					"Regimen Line",
					"Regimens(Include supported Drugs)",
					"Refill Period",
					"MMD_Type",
					"Next Appointment (yyyy-mm-dd)",
					"DSD Model"
			);
	public static final List<String> BIOMETRIC_HEADER =
			Arrays.asList(
					"S/No.",
					"State",
					"Facility Name",
					"DATIM Id",
					"Hospital Number",
					"Name",
					"Date of Birth",
					"Age",
					"Sex",
					"Address",
					"Enrollment Date (yyyy-mm-dd)",
					"Number of Fingers Captured",
					"Fingers Valid"
			);
	
	public static final List<String> LAB_HEADER =
			Arrays.asList(
					"S/No",
					"Facility Id",
					"Facility Name",
					"DATIM Id",
					"Patient Id",
					"Hospital Num",
					"Test",
					"Date Sample Collected (yyyy-mm-dd)",
					"Result",
					"Date  Result Received  (yyyy-mm-dd)"
			);
}

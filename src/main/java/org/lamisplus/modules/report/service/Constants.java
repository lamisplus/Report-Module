package org.lamisplus.modules.report.service;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
	// EXTENSION
	public static final String EXCEL_EXTENSION_XLSX = ".xlsx";
	public static final String EXCEL_EXTENSION_XLS = ".xls";
	
	// Sheet name
	public static final String PATIENT_LINE_LIST = "patient_line__list";

	public static final String CLIENT_SERVICE_LIST = "client_service_list";

	public static final String RADET_SHEET = "radet";

	public static final String HTS_SHEET = "hts";

	public static final String PREP_SHEET = "prep";
	
	// headers
	public static final List<String> PATIENT_LINE_LIST_HEADER = Arrays.asList(
			"State",
			"LGA",
			"Facility Name",
			"Datim Id",
			"Patient Id",
			"Hospital Num",
			"Unique ID",
			"Surname",
			"Other Name",
			"Date Of Birth (yyyy-mm-dd)",
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
			"Date of Registration(yyyy-mm-dd)",
			"Status at Registration",
			"ART Start Date (yyyy-mm-dd)",
			"Baseline CD4",
			"Baseline CD4p",
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
					"L.G.A",
					"LGA Of Residence",
					"Facility Name",
					"DatimId",
					"Patient ID",
					"NDR Patient Identifier",
					"Hospital Number",
					"Unique Id",
					"Household Unique No",
					"OVC Unique ID",
					"Sex",
					"Target group",
					"Current Weight (kg)",
					"Pregnancy Status",
					"Date Birth (yyyy-mm-dd)",
					//"OVC Enrollment Date (yyyy-mm-dd)",
					//"Services Provided (Enter one line per service)",
					"Age",
					"Care Entry Point",
					"Date of Registration",
					"Enrollment  Date (yyyy-mm-dd)",
					"ART Start Date (yyyy-mm-dd)",
					"Last Pickup Date (yyyy-mm-dd)",
					"Months of ARV Refill",
					"Regimen Line at ART Start",
					"Regimen at ART Start",
					"Date of Start of Current ART Regimen",
					"Current Regimen Line",
					"Current ART Regimen",
					"Clinical Staging at Last Visit",
					"Date of Last CD4 Count",
					"Last CD4 Count",
					"Date of Viral Load Sample Collection (yyyy-mm-dd)",
					"Date of Current ViralLoad Result Sample (yyyy-mm-dd)",
					"Current Viral Load (c/ml)",
					"Date of Current Viral Load (yyyy-mm-dd)",
					"Viral Load Indication",
					"Viral Load Eligibility Status",
					"Date of Viral Load Eligibility Status",
					"Current ART Status",
					"Date of Current ART Status",
					"Client Verification Status",
					//"Biometric Status",
					"Cause of Death",
					"VA Cause of Death",
					"Previous ART Status",
					"Confirmed Date of Previous ART Status",
					"ART Enrollment Setting",
					"Date of TB Screening (yyyy-mm-dd)",
					"TB status at Last Visit",
					"TB Screening Outcome",
					"Date of TB Sample Collection (yyyy-mm-dd)",
					"TB Diagnostic Test Type",
					"Date of TB Diagnostic Result Received (yyyy-mm-dd)",
					"TB Diagnostic Result",
					"Date of Start of TB Treatment (yyyy-mm-dd)",
					"TB Treatment Type (new, relapsed etc)",
					"Date of Completion of TB Treatment (yyyy-mm-dd)",
					"TB Treatment Outcome",
					"Date of TB-LAM",
					"TB-LAM result",
					"Date of TPT Start (yyyy-mm-dd)",
					"TPT Type",
					"TPT Completion date (yyyy-mm-dd)",
					"TPT Completion status",
					"Date of commencement of EAC (yyyy-mm-dd)",
					"Number of EAC Sessions Completed",
					"Date of last EAC Session Completed",
					"Date of Extended EAC Completion (yyyy-mm-dd)",
					"Date of Repeat Viral Load - Post EAC VL Sample collected (yyyy-mm-dd)",
					"Repeat Viral load result (c/ml)- POST EAC",
					"Date of Repeat Viral load result- POST EAC VL",
					"DSD Model",
					"Date Commenced DSD (yyyy-mm-dd)",
					"Date of Return of DSD Client to Facility (yyyy-mm-dd)",
					"Screening for Chronic Conditions",
					"Co-morbidities",
					"Date of Cervical Cancer Screening (yyyy-mm-dd)",
					"Cervical Cancer Screening Type",
					"Cervical Cancer Screening Method",
					"Result of Cervical Cancer Screening",
					"Date of Precancerous Lesions Treatment (yyyy-mm-dd)",
					"Precancerous Lesions Treatment Methods",
					//"Last Cryptococcal Antigen",
					//"Date of Last Cryptococcal Antigen (yyyy-mm-dd)",
					"Date Biometrics Enrolled (yyyy-mm-dd)",
					"Number of Fingers Captured",
					"Date Biometrics Recapture (yyyy-mm-dd)",
					"Number of Fingers Recaptured",
					"Valid Biometrics(Hexadecimal/Base64 Unique Identifier)",
					"Case Manager"

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
					"Phone Number",
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


	public static final List<String> HTS_HEADER =
			Arrays.asList(
					"S/No",
					"Facility Id (Datim)",
					"Facility",
					"Client Code",
					"First Name",
					"Surname",
					"Other Names",
					"Sex",
					"Target Group",
					"Age",
					"Date Of Birth (yyyy-mm-dd)",
					"Phone Number",
					"Marital Status",
					"Client Address",
					"LGA of Residence",
					"State Of Residence",
					"Education",
					"Occupation",
					"Date of Visit (yyyy-mm-dd)",
					"First Time Visit",
					"Number of wives",
					"Number of Children",
					"Index Client",
					"Previously Tested",
					"Referred From",
					"Testing Setting",
					"Counseling Type",
					"Pregnancy Status",
					"Breastfeeding",
					"Index Type",
					"If Recency Testing Opt In",
					"Recency Id",
					"Recency Test Type",
					"Recency Test Date (yyyy-mm-dd)",
					"Recency Interpretation",
					"Final Recency Result",
					"Viral Load Sample Collection Date",
					"Viral Load Result Classification",
					"Viral Load Confirmation Result",
					"Viral Load Confirmation Date (yyyy-mm-dd)",
					"Assessment Code",
					"Modality",
					"Syphilis Test Result",
					"Hepatitis B Result",
					"Hepatitis C Result",
					"CD4 Type",
					"CD4 Test Result",
					"HIV Test Result",
					"Final HIV Test Result",
					"Date Of HIV Testing (yyyy-mm-dd)",
					"Prep Offered",
					"Prep Accepted",
					"Number of condoms given",
					"Number of lubricant given",
					"HTS Latitude",
					"HTS Longitude"
			);


	public static final List<String> PrEP_HEADER =
			Arrays.asList(
					"S/No",
					"Facility Id (Datim)",
					"State",
					"LGA",
					"Facility Name",
					"Patient Identifier",
					"Hospital Number",
					"First Name",
					"Surname",
					"Other Names",
					"Sex",
					"Target Group",
					"Age",
					"Date Of Birth (yyyy-mm-dd)",
					"Phone Number",
					"Marital Status",
					"Client Address",
					"LGA of Residence",
					"State Of Residence",
					"Education",
					"Occupation",
					"Date Of Registration (yyyy-mm-dd)",
					"Date Of Commencement (yyyy-mm-dd)",
					"Baseline Regimen",
					"Baseline Systolic bp",
					"Baseline Diastolic bp",
					"Baseline Weight (kg)",
					"Baseline Height (cm)",
					"Baseline Creatinine",
					"Baseline Hepatitis B",
					"Baseline Hepatitis C",
					"HIV status at PrEP Initiation",
					"Baseline Urinalysis",
					"Baseline Urinalysis Date",
					"Indication for PrEP",
					"Current Regimen",
					"Date Of Last Pickup (yyyy-mm-dd)",
					"Current Status",
					"Date Of Current Status (yyyy-mm-dd)",
					"Current Systolic bp",
					"Current Diastolic bp",
					"Current Weight (kg)",
					"Current Height (cm)",
					"Current HIV Status",
					"Date of Current HIV Status (yyyy-mm-dd)",
					"Current Urinalysis",
					"Date of Current Urinalysis",
					"Pregnancy Status",
					"Reasons for discontinuation/Stopped",
					"Date of Discontinuation/Stopped",
					"Date Of HIV Enrollment (yyyy-mm-dd)"
			);
	
	public static final List<String> CLINIC_HEADER =
			Arrays.asList(
					"S/n",
					"Facility",
					"Datim Id",
					"Patient Id",
					"Hospital Number",
					"Date Visit",
					"Clinic Stage",
					"Function Status",
					"TB Status",
					"Body Weight (kg)",
					"Height (cm)",
					"BP (mmHg)",
					"Systolic",
					"Diastolic",
					"Pregnant Status",
					"Next Appointment"
			);
	
	public static final String CLINIC_NAME = "clinic";

	public static final List<String> CLIENT_SERVICE_HEADER =
			Arrays.asList(
					"State",
					"Facility Name",
					"Serial Enrollment No",
					"Patient Id",
					"Date of Observation",
					"Indication For Client Verification",
					"Date Of Attempt",
					"Verification Attempts",
					"Verification Status",
					"DSD Model",
					"Outcome",
					"Comment",
					"Date Returned To Care",
					"Referred To",
					"Discontinued",
					"Date of Discontinuation",
					"Reason For Discontinuation"
			);
}

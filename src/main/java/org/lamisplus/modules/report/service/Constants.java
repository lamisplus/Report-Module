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
	public static final String TB_SHEET = "tb";
	public static final String NCD_SHEET = "ncd";
	public static final String EAC_SHEET = "eac";
	public static final String HTS_SHEET = "hts";

	public static final String FAMILY_INDEX_SHEET = "family_index";

	public static final String REPORT_GENERATION_PROGRESS_TOPIC = "/topic/report-generation-progress";
	
	// headers
	public static final List<String> PATIENT_LINE_LIST_HEADER = Arrays.asList(
			"State",
			"LGA",
			"Facility Name",
			"Datim Id",
			"Patient Id",
			"Hospital Num",
			"Unique ID",
//			"Surname",
//			"Other Name",
			"Date Of Birth (yyyy-mm-dd)",
			"Age",
			"Gender",
			"Marital Status",
			"Education",
			"Occupation",
			"State of Residence",
			"Lga of Residence",
//			"Address",
//			"Phone",
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
			"DSD Type",
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
					"Date of Birth (yyyy-mm-dd)",
					//"OVC Enrollment Date (yyyy-mm-dd)",
					//"Services Provided (Enter one lineu per service)",
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
					"Client Verification Outcome",
//					"Client Verification Status",
					// "Biometric Status",
					"Cause of Death",
					"VA Cause of Death",
					"Previous ART Status",
					"Confirmed Date of Previous ART Status",
					"ART Enrollment Setting",
					"Date of TB Screening (yyyy-mm-dd)",
					"TB Screening Type",
					"TB status",
					// "TB Screening Outcome",
					"Date of TB Sample Collection (yyyy-mm-dd)",
					"TB Diagnostic Test Type",
					"Date of TB Diagnostic Result Received (yyyy-mm-dd)",
					"TB Diagnostic Result",
					"Date of Start of TB Treatment (yyyy-mm-dd)",
					"TB Type (new, relapsed etc)",
					"Date of Completion of TB Treatment (yyyy-mm-dd)",
					"TB Treatment Outcome",
					// "Date of TB-LAM",
					// "TB-LAM result",
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
					// "DSD Model",
					"Date of devolvement",
					"Model devolved to",
					"Date of current DSD",
					"Current DSD model",
					"Current DSD Outlet",
					// "Date Commenced DSD (yyyy-mm-dd)",
					"Date of Return of DSD Client to Facility (yyyy-mm-dd)",
					"Screening for Chronic Conditions",
					"Co-morbidities",
					"Date of Cervical Cancer Screening (yyyy-mm-dd)",
					"Cervical Cancer Screening Type",
					"Cervical Cancer Screening Method",
					"Result of Cervical Cancer Screening",
					"Date of Precancerous Lesions Treatment (yyyy-mm-dd)",
					"Precancerous Lesions Treatment Methods",
					// "Last Cryptococcal Antigen",
					// "Date of Last Cryptococcal Antigen (yyyy-mm-dd)",
					"Date Biometrics Enrolled (yyyy-mm-dd)",
					"Number of Fingers Captured",
					"Date Biometrics Recapture (yyyy-mm-dd)",
					"Number of Fingers Recaptured",
					// "Valid Biometrics(Hexadecimal/Base64 Unique Identifier)",
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
					"Fingers Valid",
					"Biometric Status"
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
					"Patient ID",
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
					"Entry Point",
//					"Number of wives",
//					"Number of Children",
					"Index Client",
					"Previously Tested",
					"Referred From",
					"Testing Setting",
					"Modality",
					"Counseling Type",
					"Pregnancy/Breastfeeding Status",
//					"Breastfeeding",
					"Index Type",
					"Final HIV Test Result",
					"Patient UUID",
					"Date Of HIV Testing (yyyy-mm-dd)",
//					"If Recency Testing Opt In",
					"Opt Out of RTRI?",
					"Recency Id",
					"Recency Test Type",
					"Recency Test Date (yyyy-mm-dd)",
					"Recency Interpretation",

					"Recency Viral Load Sample Collection Date",
//					"Recency Viral Load Result Classification",
					"Recency Viral Load Confirmation Result",
					"Recency Viral Load Result Received Date (yyyy-mm-dd)",
//					"Recency Viral Load Confirmation Date (yyyy-mm-dd)",
					"Final Recency Result",
//					"Assessment Code",
					"Syphilis Test Result",
					"Hepatitis B Result",
					"Hepatitis C Result",
					"CD4 Type",
					"CD4 Test Result",
					//Hackathon group 4 newly added column
					"Source",
					"Risk Score",
					"Reffered for STI",
					"Tester Code",

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
					"Prep Type",
					"Prep Distribution Setting",
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
					"Hospital Number",
					"Date of Observation",
					"Indication For Client Verification",
					"Date Of Attempt",
					"No of Verfication Attempts",
					"Verification Attempts",
//					"Verification Status",
					"Outcome",
					"DSD Model",
					"Comment",
					"Date Returned To Care",
					"Referred To",
					"Discontinued",
					"Date of Discontinuation",
					"Reason For Discontinuation",
					//Client Verification triggers
					"No initial fingerprint was captured",
					"Duplicated demographic and clinical variables",
					"Last clinical visit is over 15 months prior",
					"Incomplete visit data on the care card or pharmacy forms or EMR ",
					"Records of repeated clinical encounters, with no fingerprint recapture.",
					"Long intervals between ARV pick-ups (pick-ups more than one year apart in the same facility)",
					"Same sex, DOB and ART start date",
					"Consistently had drug pickup by proxy without viral load sample collection for two quarters",
					"Records with same services e.g ART start date and at least 3 consecutive last ART pickup dats, VL result etc",
					"Others"
			);

	public static final List<String> TB_REPORT_HEADER =
			Arrays.asList(
					"State",
					"LGA",
					"Facility",
					"Patient ID",
					"Hospital Number",
					"Unique Id",
//"Surname", "Other Names",
					"Sex",
					"Date of birth",
					"Age",
					"ART Start Date",
					"Date of Last Visit",
					"Date of TB Screening",
					"TB Screening Type",
					"TB Screening Outcome",
					"Date of TB Sample Collection (yyyy-mm-dd)",
					"Date of Specimen sent",
					"Type of specimen",
					"TB Diagnostic Evaluation",
					"Date of TB Diagnostic Evaluation",
					"TB Diagnostic Test Type",
					"Date Result Received",
					"TB Diagnostic Result",
					"Clinically Evaluated",
					"Date of Clinical Evaluation",
					"Clinical evaluation result",
					"TB Type (new, relapsed etc)",
					"Date of TB Treatment",
					"TB Treatment Outcome",
					"Date TB Treatment Outcome",
					"Eligible for TPT",
					"Contraindications for TPT",
					"Contraindication Type",
					"Date of TPT Start (yyyy-mm-dd)",
					"TPT Type",
					"TPT completion status",
					"TPT completion date",
					"Weight At Start TPT"
			);

	public static final List<String> EAC_REPORT_HEADER =
			Arrays.asList(
					"State", "L.G.A", "LGA Of Residence", "Facility Name", "DatimId", "Patient ID", "Hospital Number",
					"Unique Id", "Sex", "Target group", "Date Birth (yyyy-mm-dd)", "ART Start Date (yyyy-mm-dd)", "Regimen at ART Start",
					"Date of start of Regimen before unsuppressed Viral Load Result", "Regimen before unsuppression","Regimen Line before  unsuppression",
					"Last Pickup Date before  unsuppressed Viral Load Result", "Month of ARV Refill of the last pick up date before  unsuppressed Viral Load Result",
					"Date of Viral Load Sample Collection of unsuppressed Viral Load Result", "Recent Unsuppressed Viral Load Result", "Date of Unsuppressed Viral Load Result", "Unsuppressed Viral Load Result Indication",
					/*"Current ART Status", "Date of Current ART Status",*/ "Date of commencement of 1st EAC (yyyy-mm-dd)", "Date of  1st EAC Session Completed",
					"Method of 1st  EAC Session", "Date of commencement of 2nd  EAC (yyyy-mm-dd)", "Date of  2nd EAC Session Completed", "Method of 2nd  EAC Session",
					"Date of commencement of 3rd   EAC (yyyy-mm-dd)", "Date of  3rd EAC Session Completed", "Method of 3rd EAC Session",
					"Date of commencement of 4th  (Extended) EAC (yyyy-mm-dd)", "Date of  4th (Extended) EAC Session Completed",
					"Method of 4th (Extended) EAC Session", "Date of commencement of 5th  (Extended) EAC (yyyy-mm-dd)", "Date of  5th (Extended) EAC Session Completed",
					"Method of 5th (Extended) EAC Session", "Date of commencement of 6th  (Extended) EAC (yyyy-mm-dd)", "Date of  6th (Extended) EAC Session Completed",
					"Method of 6th (Extended) EAC Session", "Number of EAC Sessions Completed", "Date of Repeat Viral Load - Post EAC VL Sample collected (yyyy-mm-dd)",
					"Repeat Viral load result (c/ml)- POST EAC", "Date of Repeat Viral load result- POST EAC VL", "Referred to Switch Committee? 	r",
					"Eligible for Switch?", "Date of commencement of 7th (Post switch) committee) EAC (yyyy-mm-dd)", "Date of 7th (post switch committee) EAC Session Completed",
					"Method of 7th (post switch committe) EAC Session", "Date of commencement of 8th (Post switch) committee) EAC (yyyy-mm-dd)",
					"Date of 8th (post switch committee) EAC Session Completed", "Method of 8th (post switch committe) EAC Session",
					"Date of commencement of 9th (Post switch) committee) EAC (yyyy-mm-dd)", "Date of 9th (post switch committee) EAC Session Completed",
					"Method of 9th (post switch committe) EAC Session", "Date of Repeat Viral Load - Post switch EAC VL Sample collected (yyyy-mm-dd)",
					"Repeat Viral load result (c/ml)- Post Switch EAC", "Date of Repeat Viral load result- Post Switch EAC VL", "Method of post switch EAC Session",
					"Referred to Switch Committee? (Post switch EAC)", "Date Referred to Switch Committed (Post switch EAC)", "Eligible for Switch? (Post switch EAC)",
					"Date Switched", "Start date of Switched Regimen", "Switched ART Regimen Line", "Switched ART Regimen", "Case Manager"
			);

	public static final List<String> NCD_REPORT_HEADER = Arrays.asList(
			"State", "LGA", "Facility ID", "Facility Name", "Patient ID", "Hospital Number", "Unique ID",
			//"Surname", "Other Names",  "Address", "Phone",
			"Date Birth (yyyy-mm-dd)", "Age", "Sex", "Marital status", "Education",
			"Occupation", "State of Residence", "LGA of Residence", "Pregnancy Status",
			"ART Start date (yyyy-mm-dd)", "ART Regimen Line at Start", "ART Regimen at Start",
			"Current Regimen Line", "Current Regimen", "Date of Regimen Switch/Substitution (yyyy-mm-dd)",
			"Last ART Pickup date (yyyy-mm-dd)", "Months of ARV Refill", "Current ART Status",
			"Date of Current ART Status (yyyy-mm-dd)", "ART Enrollment Setting", "DDD Type",
			"Date Previously Known Hypertensive (yyyy-mm-dd)", "Date Newly Diagnosed Hypertensive (yyyy-mm-dd)",
			"Baseline Waist Circumference (cm)", "Baseline Weight (kg)", "Baseline Height (cm)", "Baseline BMI (kg/m2)",
			"Baseline Systolic (mmHg)", "Baseline Diastolic (mmHg)", "Baseline Fasting Blood Sugar (mmol/L)",
			"Baseline Random Blood Sugar (mmol/L)", "Baseline Blood Total Cholesterol (mmol/L)", "Baseline HDL (mmol/L)",
			"Baseline LDL (mmol/L)", "Baseline Sodium - Na (mmol/L)", "Baseline Potassium - K (mmol/L)",
			"Baseline Urea (mmol/L)", "Baseline Creatinine - Cr (mmol/L)", "Date of start of HTN Treatment (yyyy-mm-dd)",
			"HTN Regimen at Start", "Current HTN Regimen", "Last HTN Medication Pickup Date (yyyy-mm-dd)",
			"Months of HTN Medication Refill", "Current HTN Status", "Date of Current HTN Status (yyyy-mm-dd)",
			"Reasons for LTFU/IIT", "Current Weight (kg)", "Date of Current Weight (yyyy-mm-dd)",
			"Current Height (cm)", "Date of Current Height (yyyy-mm-dd)", "Current BMI (kg/m2)",
			"Date of Current BMI (yyyy-mm-dd)", "Current Waist Circumference (cm)",
			"Date of Current Waist Circumference (yyyy-mm-dd)", "Waist-Hip Ratio",
			"Date of Waist-Hip Ratio (yyyy-mm-dd)", "Current Systolic (mmHg)",
			"Date of Current Systolic (yyyy-mm-dd)", "Current Diastolic (mmHg)",
			"Date of Current Diastolic (yyyy-mm-dd)", "Current Fasting Blood Sugar (mmol/L)",
			"Date of Current Fasting Blood Sugar (yyyy-mm-dd)", "Current Random Blood Sugar (mmol/L)",
			"Date of Current Random Blood Sugar (yyyy-mm-dd)", "Current Blood Total Cholesterol (mmol/L)",
			"Date of Current Blood Total Cholesterol (yyyy-mm-dd)", "Current HDL (mmol/L)",
			"Date of Current HDL (yyyy-mm-dd)", "Current LDL (mmol/L)", "Date of Current LDL (yyyy-mm-dd)",
			"Current Sodium - Na (mmol/L)", "Date of Current Sodium  (yyyy-mm-dd)",
			"Current Potassium - K (mmol/L)", "Date of Current Potassium (yyyy-mm-dd)", "Current Urea (mmol/L)",
			"Date of Current Urea (yyyy-mm-dd)", "Current Creatinine - Cr (mmol/L)",
			"Date of Current Creatinine (yyyy-mm-dd)", "Date of Viral Load Sample Collection (yyyy-mm-dd)",
			"Current Viral Load (c/ml)", "Date of Current Viral Load (yyyy-mm-dd)", "Viral Load Indication"
	);


	public static final List<String> FAMILY_INDEX_HEADER = Arrays.asList(
			"State",
			"LGA",
			"Facility",
			"Facility Id (Datim)",
			"Index Client Surname",
			"Index Client Other Names",
			"Index client code/unique ID",
			"Date of birth",
			"Age",
			"Sex",
			"Marital Status",
			"Date confirmed HIV positive",
			"Date enrolled into HIV care",
			"Index client entry point",
			"Client Category",
			"Recency testing",
			"Date offered index testing",
			"Accepted Index Testing",
			"Date of Elicitation",
			"Index contact's name",
			"Index Contact's Age",
			"Index Contact's Sex",
			"Index Contact's Phone number",
			"Index Contact's Address",
			"Relationship of contact to index",
			"Type of contact",
			"History of IPV?",
			"Mode of Notification",
			"Counseling, Referral and Support services",
			"HIV Test Status",
			"HIV Test Result",
			"Date of HTS (dd-mm-yyyy)",
			"Date linked to Treatment & Care",
			"Unique ART number (UAN)",
			"Date Enrolled in OVC",
			"OVC_Id"
	);

}

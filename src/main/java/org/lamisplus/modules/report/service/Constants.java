package org.lamisplus.modules.report.service;

import java.util.Arrays;
import java.util.List;

public class Constants {

	// EXTENSION
	public static final String EXCEL_EXTENSION_XLSX = ".xlsx";
	public static final String EXCEL_EXTENSION_XLS = ".xls";

	// Sheet name
	public static final String PATIENT_LINE_LIST = "patient_line__list";

	// headers
	public static final List<String> PATIENT_LINE_LIST_HEADER = Arrays.asList(
					"Facility Id",
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
					"Last Viral Load",
					"Date of Last Viral Load (yyyy-mm-dd)",
					"Viral Load Due Date",
					"Viral Load Indication",
					// "Date Returned to Facility (yyyy-mm-dd)",
					// "Co-morbidities",
					"Case-manager"
			);
	
}

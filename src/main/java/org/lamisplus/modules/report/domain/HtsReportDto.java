package org.lamisplus.modules.report.domain;

import java.time.LocalDate;
import java.util.UUID;

public  interface HtsReportDto {
        // bio_data
        String getPersonUuid();
        String getHospitalNumber();
        Integer getAge();
        String getSex();
        LocalDate getDateOfBirth();
        String getFacilityName();
        String getLga();
        String getState();
        String getDatimId();
        String getFirstName();
        String getSurname();
        String getOtherName();
        String getPhoneNumber();
        String getMaritalStatus();
        String getAddress();
        String getOccupation();
        String getEducationStatus();
        String getLgaOfResidence();
        String getStateOfResidence();

        // htsEncounter
        String getClientCode();
        LocalDate getDateOfVisit();
        String getFinalHivTestResult();
        String getPreviouslyTestedThisYear();
        String getEntryPoint();
        String getPreviouslyTestedNegative();
        String getTestingSetting();
        String getFacilitySetting();
        String getTypeOfSession();
        String getPregnancyStatus();
        String getIndexTesting();
        String getIndexClientCode();
        String getIndexRelationship();
        String getTypeOfHivTestDone();
        String getRecencyTest();
        String getSyphilisTestResult();
        String getHivTestKitsProvided();
        String getCategoryOfClients();
        String getCondomsProvided();
        String getSource();
        String getLongitude();
        String getLatitude();
//        Integer getFacilityId();
        String getPositiveUuid();
        String getSuspected();
        // previousHts
        LocalDate getDateOfPreviousHts();
        String getPreviousHtsResult();
        // htsCounts
        Integer getNumberOfCounts();
        String getCompletedBy();
        String getFirstTimeVisit();
        String getGonModalities();
        String getPepfarModalities();
}

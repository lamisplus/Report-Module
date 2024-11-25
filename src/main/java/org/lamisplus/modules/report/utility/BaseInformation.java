package org.lamisplus.modules.report.utility;

import lombok.AllArgsConstructor;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BaseInformation {
    private final OrganisationUnitService organisationUnitService;
    private final ReportRepository reportRepository;


    public String getFacilityName(Long facilityId) {
        return organisationUnitService.getOrganizationUnit(facilityId).getName();
    }

    public String getLocalGovernmentName(Long facilityId) {
        Long parentId = reportRepository.getParentId(facilityId).get();
        return organisationUnitService.getOrganizationUnit(parentId).getName();
    }

    public String getStateName(Long facilityId) {
        Long parentId = reportRepository.getParentId(facilityId).get(); //217
        Long parentParentId = reportRepository.getParentId(parentId).get(); //10
        return organisationUnitService.getOrganizationUnit(parentParentId).getName();
    }

}

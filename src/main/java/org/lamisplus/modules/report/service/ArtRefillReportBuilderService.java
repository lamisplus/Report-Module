//package org.lamisplus.modules.report.service;
//
//import lombok.RequiredArgsConstructor;
//import org.lamisplus.modules.report.domain.dto.ArtRefillProjection;
//import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
//import org.lamisplus.modules.report.repository.ArtRefillReportRepository;
//import org.lamisplus.modules.report.utility.BaseInformation;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//
///**
// * Everything specific to THIS report lives here: which sections it has,
// * what they're titled, and where its data comes from. Rendering (Excel or
// * PDF) is entirely handled by the shared services below - this class never
// * touches POI or OpenPDF.
// */
//@Service
//@RequiredArgsConstructor
//public class ArtRefillReportBuilderService {
//
//    private final ArtRefillReportRepository repository;
//    private final ProjectionRowAssembler assembler;
//    private final BaseInformation baseInformation;
//
//    public ReportProjectionDTO build(Long facilityId, LocalDate startDate, LocalDate endDate) {
//
//        List<ArtRefillProjection> records = repository.findRefillSummary(facilityId, startDate, endDate);
//
//        return ReportProjectionDTO.builder()
//                .reportTitle("ART REFILL SUMMARY")
//                .reportingPeriod(startDate + " to " + endDate)
//                .facilityName(baseInformation.getFacilityName(facilityId))
//                .state(baseInformation.getStateName(facilityId))
//                .lga(baseInformation.getLocalGovernmentName(facilityId))
//                .sections(Collections.singletonList(
//                        assembler.buildSection(
//                                "Number of clients refilled, by regimen",
//                                "REFILLS_BY_REGIMEN",
//                                assembler.filter(records, "REFILLS_BY_REGIMEN", null)
//                        )
//                ))
//                .build();
//    }
//}

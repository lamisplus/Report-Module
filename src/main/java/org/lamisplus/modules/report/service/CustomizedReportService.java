package org.lamisplus.modules.report.service;

import lombok.SneakyThrows;
import org.lamisplus.modules.report.domain.dto.CustomizedReportDTO;
import org.lamisplus.modules.report.domain.entity.CustomizedReport;
import org.lamisplus.modules.report.repository.CustomisedReportRepository;
import org.lamisplus.modules.report.utility.ResultSetExtract;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomizedReportService {

    private final CustomisedReportRepository customisedReportRepository;
    private final ResultSetExtract resultSetExtract;
    private final ExcelService excelService;
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(INSERT|DELETE|DROP|TRUNCATE|CREATE)", Pattern.CASE_INSENSITIVE);

    public CustomizedReportService(CustomisedReportRepository customisedReportRepository, ResultSetExtract resultSetExtract, ExcelService excelService) {
        this.customisedReportRepository = customisedReportRepository;
        this.resultSetExtract = resultSetExtract;
        this.excelService = excelService;
    }

    public List<CustomizedReportDTO> findAll() {
        List<CustomizedReport> customizedReports = customisedReportRepository.findAll();
        return customizedReports.stream()
                .map(customizedReport -> {
                    CustomizedReportDTO dto = new CustomizedReportDTO();
                    BeanUtils.copyProperties(customizedReport, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CustomizedReportDTO findById(UUID id) {
        CustomizedReport customizedReport = customisedReportRepository.findById(id).orElse(null);
        CustomizedReportDTO customizedReportDTO = new CustomizedReportDTO();
        assert customizedReport != null;
        BeanUtils.copyProperties(customizedReport, customizedReportDTO);
        return customizedReportDTO;
    }

    public CustomizedReport save(CustomizedReportDTO customizedReportDTO) {
        CustomizedReport report = new CustomizedReport();
        BeanUtils.copyProperties(customizedReportDTO, report);
        return customisedReportRepository.save(report);
    }

    public void deleteById(CustomizedReportDTO customizedReportDTO) {
        CustomizedReport report = new CustomizedReport();
        BeanUtils.copyProperties(customizedReportDTO, report);
        report.setDisabled(true);
        customisedReportRepository.save(report);
    }

    @SneakyThrows
    public ByteArrayOutputStream generateCustomizedReport(String query, String reportName) {
        ResultSet resultSet = resultSetExtract.getResultSet(query);
        List<String> headers = resultSetExtract.getHeaders(resultSet);
        List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
        return excelService.generate(reportName, fullData, headers);
    }

    public boolean isQueryInvalid(String query) {
        return SQL_INJECTION_PATTERN.matcher(query).find();
    }
}

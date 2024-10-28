package org.lamisplus.modules.report.service;

import com.itextpdf.text.Font;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Service
public class DemoMsfService {

    @Autowired
    private GenerateExcelDataHelper excelService;
    private ReportRepository repository;

    private ResourceLoader resourceLoader;

    public byte[] generatePdf(List<Map<Integer, Object>> clientServiceData) throws DocumentException, IOException {
        if (clientServiceData == null || clientServiceData.isEmpty()) {
            throw new IllegalArgumentException("No data provided for PDF generation.");
        }

        // Create the PDF document
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Add title to the PDF
        addLogoPdf(document);
        addTitle(document);

        // Create the table with headers and the provided data
        PdfPTable table = createTableWithData(clientServiceData);

        // Add the table to the document
        document.add(table);

        // Close the document
        document.close();

        return baos.toByteArray();
    }

    private void addTitle(Document document) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Phrase title = new Phrase("Client Service Report", titleFont);
        document.add(title);
    }

    private PdfPTable createTableWithData(List<Map<Integer, Object>> rowData) {
        // Use the number of headers for dynamic column count
        PdfPTable table = new PdfPTable(Constants.CLIENT_SERVICE_HEADER.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Set table headers
        addTableHeader(table);

        // Add rows from the list of maps
        addRows(table, rowData);

        return table;
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        List<String> headers = Constants.CLIENT_SERVICE_HEADER;

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);
        }
    }

    private void addRows(PdfPTable table, List<Map<Integer, Object>> rowData) {
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

        for (Map<Integer, Object> row : rowData) {
            for (int i = 0; i < Constants.CLIENT_SERVICE_HEADER.size(); i++) {
                String cellData = row.getOrDefault(i, "N/A").toString(); // Default to "N/A" if value is missing
                PdfPCell dataCell = new PdfPCell(new Phrase(cellData, dataFont));
                dataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                dataCell.setPadding(5);
                table.addCell(dataCell);
            }
        }
    }

    public void addLogoPdf(Document document) throws IOException, DocumentException {
        if (resourceLoader == null) {
            throw new IllegalStateException("ResourceLoader is not initialized");
        }

        Resource resource = resourceLoader.getResource("classpath:logo.png");
        System.out.println("Got here with the " + resource);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] logoBytes = toByteArray(inputStream);  // Read the entire input stream
            Image logo = Image.getInstance(logoBytes);
            logo.scaleToFit(100, 100);
            logo.setAbsolutePosition(50, document.getPageSize().getHeight() - 120);
            document.add(logo);
        } catch (IOException e) {
            throw new IOException("Failed to load logo image", e);
        }
    }

    // Utility method for converting InputStream to byte array in Java 8
    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

//    private void addLogoPdf(Document document) throws IOException, DocumentException {
//        try {
//            Resource resource = resourceLoader.getResource("classpath:logo.png");
//            Image logo = Image.getInstance(new ClassPathResource("logo.png").getURL());
//            logo.scaleToFit(100, 100);
//            logo.setAbsolutePosition(50, document.getPageSize().getHeight() - 120);
//            document.add(logo);
//        } catch (IOException e) {
//            throw new IOException("Failed to load logo image", e);
//        }
//    }

//    public byte[] generatePdf(List<Map<Integer, Object>> rowData) throws DocumentException, IOException {
//        Document document = new Document(PageSize.A4.rotate());
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter.getInstance(document, baos);
//        document.open();
//
//        // Add title to the PDF
//        addTitle(document);
//
//        // Create the table with headers and the provided data
//        PdfPTable table = createTableWithData(rowData);
//
//        // Add the table to the document
//        document.add(table);
//
//        // Close the document
//        document.close();
//
//        return baos.toByteArray();
//    }
//
//    private void addTitle(Document document) throws DocumentException {
//        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
//        Phrase title = new Phrase("Client Service Report PDF", titleFont);
//        document.add(title);
//    }
//
//    private PdfPTable createTableWithData(List<Map<Integer, Object>> rowData) {
//        // Define the table with 7 columns, matching the number of fields in the ClientServiceDto
//        PdfPTable table = new PdfPTable(7);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10f);
//        table.setSpacingAfter(10f);
//
//        // Set table headers
//        addTableHeader(table);
//
//        // Add rows from the list of maps
//        addRows(table, rowData);
//
//        return table;
//    }
//
//    private void addTableHeader(PdfPTable table) {
//        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
//        List<String> headers = Constants.CLIENT_SERVICE_HEADER;
//
//        for (String header : headers) {
//            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
//            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//            headerCell.setPadding(8);
//            table.addCell(headerCell);
//        }
//    }
//
//    private void addRows(PdfPTable table, List<Map<Integer, Object>> rowData) {
//        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
//
//        for (Map<Integer, Object> row : rowData) {
//            for (int i = 0; i < 7; i++) {
//                String cellData = (String) row.getOrDefault(i, "N/A"); // Default to "N/A" if value is missing
//                PdfPCell dataCell = new PdfPCell(new Phrase(cellData, dataFont));
//                dataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//                dataCell.setPadding(5);
//                table.addCell(dataCell);
//            }
//        }
//    }

//
//    public byte[] generatePdf() throws DocumentException, IOException {
//        Document document = new Document(PageSize.A4);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter.getInstance(document, baos);
//        document.open();
//
//        // Add title to the PDF
//        addTitle(document);
//
//        // Create the table with headers and sample data
//        PdfPTable table = createTableWithData();
//
//        // Add the table to the document
//        document.add(table);
//
//        // Close the document
//        document.close();
//
//        return baos.toByteArray();
//    }
//
//    private void addTitle(Document document) throws DocumentException {
//        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
//        Paragraph title = new Paragraph("Sample PDF Report", titleFont);
//        title.setAlignment(Paragraph.ALIGN_CENTER);
//        title.setSpacingAfter(20);
//        document.add(title);
//    }
//
//    private PdfPTable createTableWithData() {
//        // Define the table with 5 columns
//        PdfPTable table = new PdfPTable(5);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10f);
//        table.setSpacingAfter(10f);
//
//        // Set table headers
//        addTableHeader(table);
//
//        // Add sample data rows
//        addRows(table);
//
//        return table;
//    }
//
//    private void addTableHeader(PdfPTable table) {
//        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
////        String[] headers = {"ID", "Name", "Age", "Country", "Occupation"};
//         List<String> headers = Constants.FAMILY_INDEX_HEADER;
//
//        for (String header : headers) {
//            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
//            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//            headerCell.setPadding(8);
//            table.addCell(headerCell);
//        }
//    }
//
//    private void addRows(PdfPTable table) {
//        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
//        String[][] sampleData = {
//                {"1", "John Doe", "30", "USA", "Engineer"},
//                {"2", "Jane Smith", "25", "UK", "Doctor"},
//                {"3", "Samuel Green", "35", "Canada", "Teacher"},
//                {"4", "Emily Johnson", "28", "Australia", "Designer"},
//                {"5", "Michael Brown", "40", "Germany", "Manager"}
//        };
//
//        for (String[] rowData : sampleData) {
//            for (String cellData : rowData) {
//                PdfPCell dataCell = new PdfPCell(new Phrase(cellData, dataFont));
//                dataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//                dataCell.setPadding(5);
//                table.addCell(dataCell);
//            }
//        }
//    }





//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private static final Logger logger = Logger.getLogger(DemoMsfService.class.getName());
//
//    // Enums for different aspects of the report
//    public enum DataElement {
//        TOTAL_NEW_ENROLLED("Total No. of New Beneficiaries enrolled"),
//        TOTAL_ENROLLED("Total No. of Beneficiaries currently enrolled (active)"),
//        TOTAL_CHILD_HEADED("Total No. of Child Headed Household currently enrolled"),
//        TOTAL_DECEASED("Total No. of Beneficiaries known to have died"),
//        TOTAL_MIGRATED("Total No. of Beneficiaries who have migrated"),
//        TOTAL_LOST("Total No. of Beneficiaries who are Lost-to-follow up"),
//        TOTAL_GRADUATED("Total No. of Beneficiaries graduated from programme"),
//        TOTAL_TRANSFERRED("Total No. of Beneficiaries who are Transferred"),
//        TOTAL_SERVICES("Total No. of Beneficiaries provided services");
//
//        private final String description;
//
//        DataElement(String description) {
//            this.description = description;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//    }
//
//    public enum Gender {
//        FEMALE("female"),
//        MALE("male");
//
//        private final String value;
//
//        Gender(String value) {
//            this.value = value;
//        }
//
//        public String getValue() {
//            return value;
//        }
//    }
//
//    public enum AgeGroup {
//        UNDER_1("<1"),
//        AGE_1_4("1-4"),
//        AGE_5_9("5-9"),
//        AGE_10_14("10-14"),
//        AGE_15_17("15-17"),
//        AGE_18_20("18-20"),
//        AGE_18_PLUS("18+"),
//        TOTAL("Total");
//
//        private final String label;
//
//        AgeGroup(String label) {
//            this.label = label;
//        }
//
//        public String getLabel() {
//            return label;
//        }
//    }
//
//
//
//
//
//
//    public Map<String, Object> getReportData(String state, String lga, LocalDate startDate, LocalDate endDate) {
//        try {
//          //  Map<String, Object> queryData = generateFallbackData(state, lga, startDate, endDate);
//            return generateFallbackData(state, lga, startDate, endDate);
//        } catch (Exception e) {
//            logger.warning("Failed to get data from database, using fallback data: " + e.getMessage());
//            return generateFallbackData(state, lga, startDate, endDate);
//        }
//    }
//
////    private Map<String, Object> executeQueryWithFallback(String state, String lga, LocalDate startDate, LocalDate endDate) {
////        try {
////            List<Map<String, Object>> results = jdbcTemplate.queryForList(
////                    MAIN_QUERY,
////                    state, lga, startDate, endDate,
////                    state, lga, endDate
////            );
////
////            return processQueryResults(results, state, lga, startDate, endDate);
////        } catch (Exception e) {
////            logger.warning("Database query failed: " + e.getMessage());
////            throw e;
////        }
////    }
//
//    private Map<String, Object> generateFallbackData(String state, String lga, LocalDate startDate, LocalDate endDate) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("state", state);
//        data.put("lga", lga);
//        data.put("reportingPeriod", startDate + " - " + endDate);
//        data.put("year", startDate.getYear());
//        data.put("orgCode", "ORG001");
//
//        Map<String, Map<String, Integer>> reportData = new HashMap<>();
//
//        // Initialize data structure for all data elements
//        for (DataElement element : DataElement.values()) {
//            Map<String, Integer> elementData = new HashMap<>();
//
//            // Generate dummy data for females
//            elementData.put("female_<1", 10);
//            elementData.put("female_1-4", 20);
//            elementData.put("female_5-9", 30);
//            elementData.put("female_10-14", 40);
//            elementData.put("female_15-17", 50);
//            elementData.put("female_18-20", 60);
//            elementData.put("female_18+", 70);
//            elementData.put("female_Total", 280);
//
//            // Generate dummy data for males
//            elementData.put("male_<1", 15);
//            elementData.put("male_1-4", 25);
//            elementData.put("male_5-9", 35);
//            elementData.put("male_10-14", 45);
//            elementData.put("male_15-17", 55);
//            elementData.put("male_18-20", 65);
//            elementData.put("male_18+", 75);
//            elementData.put("male_Total", 315);
//
//            // Set total
//            elementData.put("total", 595);
//
//            reportData.put(element.getDescription(), elementData);
//        }
//
//        // Special handling for child-headed households
//        Map<String, Integer> childHeadedData = reportData.get(DataElement.TOTAL_CHILD_HEADED.getDescription());
//        if (childHeadedData != null) {
//            // Set zero for age groups that should be blacked out
//            childHeadedData.put("female_<1", 0);
//            childHeadedData.put("female_1-4", 0);
//            childHeadedData.put("male_<1", 0);
//            childHeadedData.put("male_1-4", 0);
//        }
//
//        data.put("reportData", reportData);
//        return data;
//    }
//
//    private Map<String, Object> executeQueryWithFallback(String state, String lga, LocalDate startDate, LocalDate endDate) {
//        try {
//            List<Map<String, Object>> results = jdbcTemplate.queryForList(
//                    MAIN_QUERY,
//                    state, lga, startDate, endDate,
//                    state, lga, endDate
//            );
//
//            return processQueryResults(results, state, lga, startDate, endDate);
//        } catch (Exception e) {
//            logger.warning("Database query failed: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private Map<String, Object> processQueryResults(List<Map<String, Object>> results,
//                                                    String state,
//                                                    String lga,
//                                                    LocalDate startDate,
//                                                    LocalDate endDate) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("state", state);
//        data.put("lga", lga);
//        data.put("reportingPeriod", startDate + " - " + endDate);
//        data.put("year", startDate.getYear());
//        data.put("orgCode", "ORG001");
//
//        Map<String, Map<String, Integer>> reportData = new HashMap<>();
//
//        // Initialize data structure for all data elements
//        for (DataElement element : DataElement.values()) {
//            Map<String, Integer> elementData = new HashMap<>();
//            for (Gender gender : Gender.values()) {
//                for (AgeGroup ageGroup : AgeGroup.values()) {
//                    elementData.put(gender.getValue() + "_" + ageGroup.getLabel(), 0);
//                }
//            }
//            elementData.put("total", 0);
//            reportData.put(element.getDescription(), elementData);
//        }
//
//        // Process query results
//        for (Map<String, Object> row : results) {
//            String metric = (String) row.get("metric");
//            String gender = (String) row.get("gender");
//            String ageGroup = (String) row.get("age_group");
//            int count = ((Number) row.get("count")).intValue();
//
//            DataElement element = getDataElementForMetric(metric);
//            if (element != null) {
//                Map<String, Integer> elementData = reportData.get(element.getDescription());
//                if (elementData != null) {
//                    // Update specific age group count
//                    String key = gender + "_" + ageGroup;
//                    elementData.put(key, count);
//
//                    // Update gender total
//                    String genderTotalKey = gender + "_Total";
//                    int currentGenderTotal = elementData.getOrDefault(genderTotalKey, 0);
//                    elementData.put(genderTotalKey, currentGenderTotal + count);
//
//                    // Update overall total
//                    int currentTotal = elementData.getOrDefault("total", 0);
//                    elementData.put("total", currentTotal + count);
//                }
//            }
//        }
//
//        // Special handling for child-headed households
//        Map<String, Integer> childHeadedData = reportData.get(DataElement.TOTAL_CHILD_HEADED.getDescription());
//        if (childHeadedData != null) {
//            // Set zero for age groups that should be blacked out
//            childHeadedData.put("female_<1", 0);
//            childHeadedData.put("female_1-4", 0);
//            childHeadedData.put("male_<1", 0);
//            childHeadedData.put("male_1-4", 0);
//        }
//
//        // Recalculate totals for each element
//        for (Map<String, Integer> elementData : reportData.values()) {
//            int femaleTotal = 0;
//            int maleTotal = 0;
//
//            // Calculate gender totals
//            for (AgeGroup ageGroup : AgeGroup.values()) {
//                if (ageGroup != AgeGroup.TOTAL) {
//                    femaleTotal += elementData.getOrDefault("female_" + ageGroup.getLabel(), 0);
//                    maleTotal += elementData.getOrDefault("male_" + ageGroup.getLabel(), 0);
//                }
//            }
//
//            // Update gender totals
//            elementData.put("female_Total", femaleTotal);
//            elementData.put("male_Total", maleTotal);
//
//            // Update overall total
//            elementData.put("total", femaleTotal + maleTotal);
//        }
//
//        data.put("reportData", reportData);
//        return data;
//    }
//
//    private DataElement getDataElementForMetric(String metric) {
//        if (metric == null) return null;
//
//        switch (metric.toLowerCase()) {
//            case "new_beneficiaries":
//                return DataElement.TOTAL_NEW_ENROLLED;
//            case "current_beneficiaries":
//                return DataElement.TOTAL_ENROLLED;
//            case "child_headed":
//                return DataElement.TOTAL_CHILD_HEADED;
//            case "deceased":
//                return DataElement.TOTAL_DECEASED;
//            case "migrated":
//                return DataElement.TOTAL_MIGRATED;
//            case "lost":
//                return DataElement.TOTAL_LOST;
//            case "graduated":
//                return DataElement.TOTAL_GRADUATED;
//            case "transferred":
//                return DataElement.TOTAL_TRANSFERRED;
//            case "services_provided":
//                return DataElement.TOTAL_SERVICES;
//            default:
//                return null;
//        }
//    }
//    private static final String MAIN_QUERY = "";
//
//    public byte[] generatePdfReport(Map<String, Object> data) throws IOException, DocumentException {
//        Document document = new Document(PageSize.A4.rotate());
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter writer = PdfWriter.getInstance(document, baos);
//        document.open();
//
//
////        addLogoPdf(document);
//        createHeaderPdf(document, data);
//        createTableHeaderPdf(document);
//        populateDataPdf(document, data);
//
//        document.close();
//        return baos.toByteArray();
//    }
//
//    private boolean shouldUseBlackStyle(DataElement element, AgeGroup ageGroup) {
//        return element == DataElement.TOTAL_CHILD_HEADED &&
//                (ageGroup == AgeGroup.UNDER_1 || ageGroup == AgeGroup.AGE_1_4);
//    }
//
//    private void addLogoPdf(Document document) throws IOException, DocumentException {
//        try {
//            Image logo = Image.getInstance(new ClassPathResource("logo.png").getURL());
//            logo.scaleToFit(100, 100);
//            logo.setAbsolutePosition(50, document.getPageSize().getHeight() - 120);
//            document.add(logo);
//        } catch (IOException e) {
//            throw new IOException("Failed to load logo image", e);
//        }
//    }
//
//    private void createHeaderPdf(Document document, Map<String, Object> data) throws DocumentException {
//        BaseFont baseFont = null;
//        try {
//            baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//        } catch (Exception e) {
//            throw new DocumentException("Failed to create base font", e);
//        }
//
//        Font headerFont = new Font(baseFont, 14, Font.BOLD, BaseColor.DARK_GRAY);
//        Font subHeaderFont = new Font(baseFont, 12, Font.NORMAL);
//
//        // Add main header
//        Paragraph header = new Paragraph("CSO VULNERABLE HOUSEHOLD/ CHILDREN MONTHLY/QUATERLY SUMMARY FORM", headerFont);
//        header.setAlignment(Element.ALIGN_CENTER);
//        header.setSpacingBefore(60);
//        header.setSpacingAfter(20);
//        document.add(header);
//
//        // Add subheader information
//        float[] columnWidths = {1f, 1f, 1f};
//        PdfPTable subHeaderTable = new PdfPTable(columnWidths);
//        subHeaderTable.setWidthPercentage(100);
//        subHeaderTable.setSpacingBefore(10);
//        subHeaderTable.setSpacingAfter(10);
//
//        // Add subheader cells
//        addSubHeaderCell(subHeaderTable, "State: " + data.get("state"), subHeaderFont);
//        addSubHeaderCell(subHeaderTable, "LGA: " + data.get("lga"), subHeaderFont);
//        addSubHeaderCell(subHeaderTable, "Reporting Period: " + data.get("reportingPeriod"), subHeaderFont);
//        addSubHeaderCell(subHeaderTable, "Year: " + data.get("year"), subHeaderFont);
//        addSubHeaderCell(subHeaderTable, "Organization Code: " + data.get("orgCode"), subHeaderFont);
//
//        document.add(subHeaderTable);
//    }
//
//    private void addSubHeaderCell(PdfPTable table, String text, Font font) {
//        PdfPCell cell = new PdfPCell(new Phrase(text, font));
//        cell.setBorder(Rectangle.NO_BORDER);
//        cell.setPadding(5);
//        table.addCell(cell);
//    }
//
//    private void createTableHeaderPdf(Document document) throws DocumentException {
//        float[] columnWidths = new float[18];
//        columnWidths[0] = 4f; // First column wider for descriptions
//        for (int i = 1; i < 18; i++) {
//            columnWidths[i] = 1f;
//        }
//
//        PdfPTable table = new PdfPTable(columnWidths);
//        table.setWidthPercentage(100);
//
//        // Create fonts
//        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
//        Font subHeaderFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
//
//        // Add main header row
//        PdfPCell mainHeader = new PdfPCell(new Phrase("Community VC Services Data Element", headerFont));
//        mainHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        mainHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
//        mainHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        table.addCell(mainHeader);
//
//        // Add Female section header
//        PdfPCell femaleHeader = new PdfPCell(new Phrase("Female", headerFont));
//        femaleHeader.setColspan(8);
//        femaleHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        femaleHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(femaleHeader);
//
//        // Add Male section header
//        PdfPCell maleHeader = new PdfPCell(new Phrase("Male", headerFont));
//        maleHeader.setColspan(8);
//        maleHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        maleHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(maleHeader);
//
//        // Add Total header
//        PdfPCell totalHeader = new PdfPCell(new Phrase("Total", headerFont));
//        totalHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        totalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(totalHeader);
//
//        // Add age group headers
//        String[] ageGroups = {"<1", "1-4", "5-9", "10-14", "15-17", "18-20", "18+", "Total"};
//
//        // Female age groups
//        for (String ageGroup : ageGroups) {
//            PdfPCell cell = new PdfPCell(new Phrase(ageGroup, subHeaderFont));
//            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell);
//        }
//
//        for (String ageGroup : ageGroups) {
//            PdfPCell cell = new PdfPCell(new Phrase(ageGroup, subHeaderFont));
//            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell);
//        }
//
//        document.add(table);
//    }
//
//    private void populateDataPdf(Document document, Map<String, Object> data) throws DocumentException {
//        float[] columnWidths = new float[18];
//        columnWidths[0] = 4f;
//        for (int i = 1; i < 18; i++) {
//            columnWidths[i] = 1f;
//        }
//
//        PdfPTable table = new PdfPTable(columnWidths);
//        table.setWidthPercentage(100);
//
//        Font dataFont = new Font(Font.FontFamily.HELVETICA, 8);
//        Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
//
//        @SuppressWarnings("unchecked")
//        Map<String, Map<String, Integer>> reportData = (Map<String, Map<String, Integer>>) data.getOrDefault("reportData", new HashMap<>());
//
//        for (DataElement element : DataElement.values()) {
//            // Add row header
//            PdfPCell headerCell = new PdfPCell(new Phrase(element.getDescription(), headerFont));
//            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            headerCell.setPadding(5);
//            table.addCell(headerCell);
//
//            Map<String, Integer> rowData = reportData.getOrDefault(element.getDescription(), new HashMap<>());
//
//            // Add female data
//            addGenderDataCells(table, rowData, Gender.FEMALE, element, dataFont);
//
//            // Add male data
//            addGenderDataCells(table, rowData, Gender.MALE, element, dataFont);
//
//            // Add total
//            PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(rowData.getOrDefault("total", 0)), dataFont));
//            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(totalCell);
//        }
//
//        document.add(table);
//    }
//
//    private void addGenderDataCells(PdfPTable table, Map<String, Integer> rowData,
//                                    Gender gender, DataElement element, Font dataFont) {
//        for (AgeGroup ageGroup : AgeGroup.values()) {
//            if (ageGroup != AgeGroup.TOTAL) {
//                String key = gender.getValue() + "_" + ageGroup.getLabel();
//                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(rowData.getOrDefault(key, 0)), dataFont));
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//                if (shouldUseBlackStyle(element, ageGroup)) {
//                    cell.setBackgroundColor(BaseColor.BLACK);
//                }
//
//                table.addCell(cell);
//            }
//        }
//        // Add gender total
//        String totalKey = gender.getValue() + "_" + AgeGroup.TOTAL.getLabel();
//        PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(rowData.getOrDefault(totalKey, 0)), dataFont));
//        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(totalCell);
//    }

//    private byte[] writeToByteArray(XSSFWorkbook workbook) throws IOException {
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            workbook.write(outputStream);
//            return outputStream.toByteArray();
//        }
//    }
}
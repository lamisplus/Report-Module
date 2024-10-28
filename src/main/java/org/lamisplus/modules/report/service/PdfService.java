//package org.lamisplus.modules.report.service;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Stream;
//
//@Service
//public class PdfService {// Method to generate a PDF and return as a byte array
//
//
//    public byte[] generatePdfReport(List<Map<Integer, Object>> data) {
//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//
//            // Initialize PDF writer
////            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
//
//            // Initialize PDF document
////            PdfDocument pdfDoc = new PdfDocument(writer);
//            Document document = new Document();
//
//            // Initialize document layout
////            Document document = new Document();
//            PdfWriter.getInstance(document, byteArrayOutputStream);
//
//            List<String> projections = Constants.EAC_REPORT_HEADER;
//
//            PdfPTable table = new PdfPTable(projections.size());
//
//            PdfPCell header = new PdfPCell();
//            Stream.of(projections).forEach(title -> {
//                header.setBackgroundColor(BaseColor.CYAN);
//                header.setBorderWidth(1);
//
////                PdfPCell header = new PdfPCell();
//            });
//
//
//
//
//                // Add table to document
//                document.add(table);
//                document.close();
//            } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        } catch (DocumentException ex) {
//            throw new RuntimeException(ex);
//        }
//            // Close document
////            document.close();
//
//            // Return PDF as byte array
//            return byteArrayOutputStream.toByteArray();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    p
//}
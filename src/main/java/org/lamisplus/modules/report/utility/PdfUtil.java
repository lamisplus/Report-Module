package org.lamisplus.modules.report.utility;

//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PdfUtil {

    public static <T> void generatePdfFromObject(T object, String filePath) {
        Document document = new Document();
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath))) {
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();

            // Add title to the document
//            document.add(new Paragraph("Object Data").setF.setFontSize(18));

            // Convert object data to string and add it to the PDF
            String objectData = getObjectDataAsString(object);
            document.add(new Paragraph(objectData));

            document.close();
            System.out.println("PDF generated successfully.");

        } catch (IOException | DocumentException e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }

    /**
     * Converts an object's data to a string representation.
     *
     * @param object the object to convert
     * @return string representation of the object's data
     */
    private static <T> String getObjectDataAsString(T object) {
        if (object == null) {
            return "null";
        } else if (object instanceof Map) {
            return mapToString((Map<?, ?>) object);
        } else if (object instanceof List) {
            return listToString((List<?>) object);
        } else {
            return object.toString();
        }
    }

    /**
     * Converts a map to a string representation.
     *
     * @param map the map to convert
     * @return string representation of the map
     */
    private static String mapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        return sb.toString();
    }

    /**
     * Converts a list to a string representation.
     *
     * @param list the list to convert
     * @return string representation of the list
     */
    private static String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(item -> sb.append(item).append("\n"));
        return sb.toString();
    }







    // Method to generate PDF from a simple POJO object
//    public static <T> void generatePdfFromObject(T object, String filePath) {
//        try {
//            // Initialize PDF writer
//            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
//            // Initialize PDF document
//            PdfDocument pdfDocument = new PdfDocument(writer);
//            // Initialize document layout
//            Document document = new Document(pdfDocument);
//
//            // Add title to the document
//            document.add(new Paragraph("Object Data").setBold().setFontSize(18));
//
//            // Convert object data to string and add it to the PDF
//            String objectData = getObjectDataAsString(object);
//            document.add(new Paragraph(objectData));
//
//            // Close document
//            document.close();
//            System.out.println("PDF generated successfully.");
//        } catch (IOException e) {
//            System.out.println("Error generating PDF: " + e.getMessage());
//        }
//    }

//    public static <T> void generatePdfFromObject(T object, String filePath) {
//        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
//             PdfDocument pdfDocument = new PdfDocument(writer);
//             Document document = new Document(pdfDocument)) {
//
//            // Add title to the document
//            document.add(new Paragraph("Object Data").setBold().setFontSize(18));
//
//            // Convert object data to string and add it to the PDF
//            String objectData = getObjectDataAsString(object);
//            document.add(new Paragraph(objectData));
//
//            System.out.println("PDF generated successfully.");
//
//        } catch (IOException e) {
//            System.out.println("Error generating PDF: " + e.getMessage());
//        }
//    }
//
//    // Utility method to convert object to string format
//    private static <T> String getObjectDataAsString(T object) {
//        StringBuilder data = new StringBuilder();
//        for (var field : object.getClass().getDeclaredFields()) {
//            field.setAccessible(true); // Bypass private field access
//            try {
//                data.append(field.getName()).append(": ").append(field.get(object)).append("\n");
//            } catch (IllegalAccessException e) {
//                data.append(field.getName()).append(": [ERROR ACCESSING FIELD]\n");
//            }
//        }
//        return data.toString();
//    }
}
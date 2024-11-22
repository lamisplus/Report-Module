package org.lamisplus.modules.report.utility;

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

}
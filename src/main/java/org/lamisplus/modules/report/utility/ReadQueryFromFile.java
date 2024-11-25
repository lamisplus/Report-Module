package org.lamisplus.modules.report.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadQueryFromFile {

    public static String readQuery(String filePath) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = ReadQueryFromFile.class.getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}

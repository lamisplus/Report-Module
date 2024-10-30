package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@Slf4j
@RequiredArgsConstructor
public class PmtctService {

    private final JdbcTemplate jdbcTemplate;


    public JsonNode fetchPMTCTMSFReport() throws IOException {

        String queryPath = "pmtctMsfReport.sql";
        log.info("Query Path: {}", queryPath);

        // Read the SQL query from the file in the classpath
        String query = readSqlFromClasspath(queryPath);
        log.info("Executing query: {}", query);

        // Execute the query and fetch results using JdbcTemplate
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // Convert the result into a structured JSON node
        ObjectMapper mapper = new ObjectMapper();

        // Build a structured JSON with a "report" key wrapping the result
        ObjectNode root = mapper.createObjectNode();
        ArrayNode reportArray = mapper.valueToTree(result);

        // Add the array of results under the "report" key
        root.set("pmtct_msf_report", reportArray);

        return root;
    }

    // read the pmtct msf query  from a file in the classpath
    private String readSqlFromClasspath(String queryPath) throws IOException {
        InputStream inputStream = new ClassPathResource(queryPath).getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }


}

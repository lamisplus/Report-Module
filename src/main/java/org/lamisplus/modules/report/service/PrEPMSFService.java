package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrEPMSFService {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

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

    public JsonNode fetchPrEPMSFReport1(Long facilityId, LocalDate startDate, LocalDate endDate) throws IOException {
        String queryPath = "prep_msf_query.sql";

        // Read the SQL query from the file in the classpath
        String query = readSqlFromClasspath(queryPath);

        // Define parameters for the query
        Map<String, Object> params = new HashMap<>();
        params.put("facilityId", facilityId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        // Execute the query and fetch results
        log.info("With parameters: facilityId={}, startDate={}, endDate={}", facilityId, startDate, endDate);
        List<Map<String, Object>> result = namedJdbcTemplate.queryForList(query, params);

        // Convert the result into a structured JSON node
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode reportArray = mapper.valueToTree(result);

        // Add the array of results under the "report" key
        root.set("prep_msf_query", reportArray);

        return root;
    }
}
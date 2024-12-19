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

    public JsonNode fetchPrEPMSFReport(Long facilityId, LocalDate startDate, LocalDate endDate) throws IOException {
        String queryPath = "prep_msf_query.sql";

        String query = readSqlFromClasspath(queryPath);

        Map<String, Object> params = new HashMap<>();
        params.put("facilityId", facilityId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        log.info("With parameters: facilityId={}, startDate={}, endDate={}", facilityId, startDate, endDate);
        List<Map<String, Object>> result = namedJdbcTemplate.queryForList(query, params);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode reportArray = mapper.valueToTree(result);

        root.set("prep_msf_query", reportArray);

        return root;
    }
}
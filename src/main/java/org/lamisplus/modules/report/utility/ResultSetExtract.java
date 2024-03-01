package org.lamisplus.modules.report.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ResultSetExtract {
    private final DataSource dataSource;

    public ResultSet getResultSet(String query) throws SQLException {
        ResultSet resultSet;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            LOG.info("query is - {}", query);
            resultSet = stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(conn != null) conn.close();
        }
        return resultSet;
    }
    public List<String> getHeaders(ResultSet rs){
        List<String> headers = new ArrayList<>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int index = 1; index <= columnCount; index++) {
                String column = rsmd.getColumnName(index);
                LOG.info("column header is - {}", column);
                headers.add(column);
            }
            return headers;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public List<Map<Integer, Object>> getQueryValues(ResultSet rs, String excludeColumn) throws SQLException {
        return ResultSetToJsonMapper.mapResultSet(rs, excludeColumn);
    }
}

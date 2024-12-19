package org.lamisplus.modules.report.utility;

// convenient JDBC result set to JSON array mapper

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class ResultSetToJsonMapper {


    /**
     * maps ResultSet for database to Json Array.
     * @param resultSet
     * @param excludedColumn
     * @return JSONArray
     */
    public static List<Map<Integer, Object>> mapResultSet(ResultSet resultSet, String excludedColumn) throws SQLException, JSONException {
        JSONArray jArray = new JSONArray();
        JSONObject jsonObject = null;
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();
        List<Map<Integer, Object>> result = new ArrayList<>();

        while(resultSet.next())
        {
            Map<Integer, Object> map = new HashMap<>();
                for (int index = 1; index <= columnCount; index++) {
                    String column = rsmd.getColumnName(index);
                    if (excludedColumn != null && excludedColumn.contains(column)) {
                        continue;
                    }
                    Object value = resultSet.getObject(column);
                    map.put(index, value);
                }
            result.add(map);
        }

        return result;
    }

    public static List<List> getPages(List list, Integer pageSize) {
        if (list == null || list.isEmpty() || list.size() < 1) return Collections.emptyList();
        if (pageSize == null || pageSize <= 0 || pageSize > list.size())
            pageSize = list.size();
        int numPages = (int) Math.ceil((double)list.size() / (double)pageSize);
        List<List> pages = new ArrayList<>(numPages);
        for (int pageNum = 0; pageNum < numPages;)
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        return pages;
    }
}
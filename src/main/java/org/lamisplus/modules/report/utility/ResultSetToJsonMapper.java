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
            //jsonObject = new JSONObject();
            //if(resultSet.next()) {
                for (int index = 1; index <= columnCount; index++) {
                    String column = rsmd.getColumnName(index);
                    //LOG.info("column is {}", column);
                    //exclude column
                    if (excludedColumn != null && excludedColumn.contains(column)) {
                        continue;
                    }


                    Object value = resultSet.getObject(column);
                    //LOG.info("Object is {}", value);
                    /*if (value == null) {
                        value = "";
                        jsonObject.put(column, value);
                    } else if (value instanceof Integer) {
                        jsonObject.put(column, (Integer) value);
                    } else if (value instanceof String) {
                        jsonObject.put(column, (String) value);
                    } else if (value instanceof Boolean) {
                        jsonObject.put(column, (Boolean) value);
                    } else if (value instanceof Date) {
                        jsonObject.put(column, value.toString());
                    } else if (value instanceof Long) {
                        jsonObject.put(column, (Long) value);
                    } else if (value instanceof Double) {
                        jsonObject.put(column, (Double) value);
                    } else if (value instanceof Float) {
                        jsonObject.put(column, (Float) value);
                    } else if (value instanceof BigDecimal) {
                        jsonObject.put(column, (BigDecimal) value);
                    } else if (value instanceof Byte) {
                        jsonObject.put(column, (Byte) value);
                    } else if (value instanceof byte[]) {
                        jsonObject.put(column, (byte[]) value);
                    } else if (rsmd.getColumnType(index) == 1111) {
                        jsonObject.put(column, value);
                    } else {
                        throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
                    }*/
                    map.put(index, value);
                }
            //}
            result.add(map);
        }//while(resultSet.next());

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
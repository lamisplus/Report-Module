package org.lamisplus.modules.report.service;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.audit4j.core.util.Log;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
	
	private SXSSFWorkbook workbook;
	
	private Sheet sheet;
	
	
	
	
	
	private void writeHeader(String sheetName, List<String> headers ) {
		sheet = workbook.createSheet(sheetName);
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		Font font  = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		style.setFont(font);
		for (int i = 0; i < headers.size(); i++) {
			// O(n)T  || O(1)
			createCell(row, i, headers.get(i),style);
		}
	}
	
	private int createCellHeader(Row row, int columnCount, String value){
		Font font  = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		Cell cell = row.createCell(columnCount);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return 0;
	}
	
	private int createCell(Row row, int columnCount, Object value, CellStyle style ) {
		Cell cell = row.createCell(columnCount);
		cell.setCellStyle(null);
		if (value instanceof Integer) {
			cell.setCellValue(((int) value));
			formatTheCell(style, "0");
			cell.setCellStyle(style);
			return 0;
		} else if (value instanceof Long) {
			cell.setCellValue((long) value);
			formatTheCell(style, "0");
			cell.setCellStyle(style);
			return 1;
		} else if(value instanceof Double){
			cell.setCellValue((double) value);
			String customFormat = "#,##0";
			formatTheCell(style, customFormat);
			cell.setCellStyle(style);
			return 2;
		}else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
			cell.setCellStyle(style);
			return 3;
		} else if(value instanceof Date){
			cell.setCellValue(((Date) value).toLocalDate());
			formatTheCell(style, "yyyy-MM-dd");
			cell.setCellStyle(style);
			return 4;
		}else if(value instanceof LocalDate){
			cell.setCellValue((LocalDate) value);
			formatTheCell(style, "yyyy-MM-dd");
			cell.setCellStyle(style);
			return 5;
		}else if(value instanceof LocalDateTime){
			cell.setCellValue(((LocalDateTime) value).toLocalDate());
			formatTheCell(style, "yyyy-MM-dd");
			cell.setCellStyle(style);
			return 6;
		} else if(value instanceof Timestamp) {
			cell.setCellValue(new Date(((Timestamp) value).getTime()));
			formatTheCell(style, "yyyy-MM-dd");
			cell.setCellStyle(style);
			return 7;
		}else {
		    cell.setCellValue((String) value);
			cell.setCellStyle(style);
			return 8;
		}
		
	}
	
	private void formatTheCell(CellStyle style, String s) {
		CreationHelper createHelper = workbook.getCreationHelper();
		style.setDataFormat(createHelper.createDataFormat().getFormat(s));
	}
	
	private void write(List<Map<Integer, Object>> listData) {
		int rowCount = 1;
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		
		for (Map<Integer, Object> map : listData) { // you may be thinking O(n^2) but actually it is O(n)
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			for (Iterator<Integer> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
				Integer key = iterator.next();
				createCell(row, columnCount++, map.get(key), style);
			}
		}
	}
	
	public ByteArrayOutputStream generate(
			String sheetName,
			List<Map<Integer, Object>> listData,
			List<String> headers)  {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			this.workbook = new SXSSFWorkbook(1000);
			writeHeader(sheetName, headers);
			write(listData);
			Log.info("last row {}", workbook.getSheet(sheetName).getLastRowNum());
			workbook.write(bao);
			//FileOutputStream fileOut = new FileOutputStream("runtime/" + sheetName + ".xlsx");
			//workbook.write(fileOut);
			//workbook.close();
			//bao.close();
			//return bao;
		}catch (Exception e){
			e.printStackTrace();
		}
		return bao;
	}
	
	
	
}

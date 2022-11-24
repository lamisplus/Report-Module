package org.lamisplus.modules.report.service;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.audit4j.core.util.Log;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
	
	private SXSSFWorkbook workbook;
	
	private Sheet sheet;
	
	
	
	
	
	private void writeHeader(String sheetName, List<String> headers) {
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
			createCell(row, i, headers.get(i), style);
		}
	}
	
	private int createCell(Row row, int columnCount, Object value, CellStyle style) {
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
			cell.setCellStyle(style);
			return 0;
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
			cell.setCellStyle(style);
			return 1;
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
			cell.setCellStyle(style);
			return 2;
		} else if(value instanceof LocalDate){
			cell.setCellValue((LocalDate) value);
			cell.setCellStyle(style);
			return 3;
		}else {
		    cell.setCellValue((String) value);
			cell.setCellStyle(style);
			return 4;
		}
		
	}
	
	private void write(List<Map<Integer, String>> listData) {
		int rowCount = 1;
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		
		for (Map<Integer, String> map : listData) {
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
			List<Map<Integer, String>> listData,
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

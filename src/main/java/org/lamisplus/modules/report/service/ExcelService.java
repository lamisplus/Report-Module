package org.lamisplus.modules.report.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.audit4j.core.util.Log;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
	
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	
	
	
	public ExcelService() {
		this.workbook = new XSSFWorkbook();
	}
	
	private void writeHeader(String sheetName, List<String> headers) {
		sheet = workbook.createSheet(sheetName);
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(15);
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
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		for (Map<Integer, String> map : listData) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			for (Integer key : map.keySet()) {
				createCell(row, columnCount++, map.get(key), style);
			}
			sheet.autoSizeColumn(columnCount);
		}
	}
	
	public ByteArrayOutputStream generate(
			String sheetName,
			List<Map<Integer, String>> listData,
			List<String> headers)  {
		try {
			writeHeader(sheetName, headers);
			write(listData);
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			workbook.write(bao);
			Log.info("last row {}", workbook.getSheet(sheetName).getLastRowNum());
			FileOutputStream fileOut = new FileOutputStream("runtime/" + sheetName + ".xlsx");
			workbook.write(fileOut);
			workbook.close();
			bao.close();
			return bao;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}

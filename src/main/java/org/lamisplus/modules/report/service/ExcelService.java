package org.lamisplus.modules.report.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.audit4j.core.util.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
@Slf4j
public class ExcelService {
	
	private SXSSFWorkbook workbook;
	
	private Sheet sheet;

	private final SimpMessageSendingOperations messagingTemplate;

	public ExcelService(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}


	private void writeHeader(String sheetName, List<String> headers ) {
		sheet = workbook.createSheet(sheetName);
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		Font font = getFont();
		style.setFont(font);
		for (int i = 0; i < headers.size(); i++) {
			createCell(row, i, headers.get(i),style);
		}
	}
	
	private int createCellHeader(Row row, int columnCount, String value){
		Font font = getFont();
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		Cell cell = row.createCell(columnCount);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return 0;
	}
	
	@NotNull
	private Font getFont() {
		Font font  = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
		return font;
	}
	
	private int createCell(Row row, int columnCount, Object value, CellStyle style ) {
		Cell cell = row.createCell(columnCount);
		cell.setCellStyle(null);
		if (value instanceof Integer) {
			cell.setCellValue(((int) value));
			cell.setCellStyle(style);
			return 0;
		} else if (value instanceof Long) {
			cell.setCellValue((long) value);
			cell.setCellStyle(style);
			return 1;
		} else if(value instanceof Double){
			cell.setCellValue((double) value);
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
		    cell.setCellValue(replaceNull(value));
			cell.setCellStyle(style);
			return 8;
		}
	}

	private String replaceNull(Object value){
		return String.valueOf(value).replace("null", "").replace("NULL", "");
	}
	
	
	private  void getDoubleFormat(CellStyle style) {
		StringBuilder formatBuilder = new StringBuilder("#,##0");
		formatBuilder.append(".");
		for (int i = 0; i < 2; i++) {
			formatBuilder.append("0");
		}
		String format = formatBuilder.toString();
		style.setDataFormat(workbook.createDataFormat().getFormat(format));
	}
	private void formatTheCell(CellStyle style, String s) {
		CreationHelper createHelper = workbook.getCreationHelper();
		style.setDataFormat(createHelper.createDataFormat().getFormat(s));
	}
	
	private void write(List<Map<Integer, Object>> listData) {
		int rowCount = 1;
		CellStyle nonNumericStyle = getNonNumericStyle();
		CellStyle numericStyle = workbook.createCellStyle();
		numericStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,00"));
		numericStyle.setAlignment(HorizontalAlignment.LEFT);
		CellStyle doubleStyle = workbook.createCellStyle();
		getDoubleFormat(doubleStyle);
		doubleStyle.setAlignment(HorizontalAlignment.LEFT);
		
		for (Map<Integer, Object> map : listData) { // you may be thinking O(n^2) but actually it is O(n)
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			for (Iterator<Integer> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
				Integer key = iterator.next();
				Object value = map.get(key);
				if (value instanceof Double || value instanceof Integer || value instanceof Long) {
					if(value instanceof Integer || value instanceof Long){
						createCell(row, columnCount++, value, numericStyle);
					}else {
						createCell(row, columnCount++, value, doubleStyle);
					}
				} else {
					createCell(row, columnCount++, value, nonNumericStyle);
				}

			}
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Written " + rowCount + " of " + listData.size() + " rows ... ");

		}
	}
	
	@NotNull
	private CellStyle getNonNumericStyle() {
		CellStyle nonNumericStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		nonNumericStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		nonNumericStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
		nonNumericStyle.setAlignment(HorizontalAlignment.LEFT);
		nonNumericStyle.setFillPattern(FillPatternType.FINE_DOTS);
		nonNumericStyle.setFont(font);
		return nonNumericStyle;
	}
	
	public ByteArrayOutputStream generate(
			String sheetName,
			List<Map<Integer, Object>> listData,
			List<String> headers)  {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			this.workbook = new SXSSFWorkbook(1000);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Writing report headers ... ");
			writeHeader(sheetName, headers);
			write(listData);
			Log.info("last row {}", workbook.getSheet(sheetName).getLastRowNum());
			workbook.write(bao);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Writing report completed ... ");
		}catch (Exception e){
			e.printStackTrace();
		}
		return bao;
	}
	
	
	
}

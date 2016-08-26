package com.pat.app.cwtool.batch;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.pat.app.cwtool.model.FinanceRecord;

public abstract class OutputUnmatchedFinanceRecordsTasklet extends
		BaseOuputUnmatchedRecordsTasklet<FinanceRecord> {

	public OutputUnmatchedFinanceRecordsTasklet() {
		super();
	}

	@Override
	protected void createDataRow(FinanceRecord record, Row row) {
		int cursor = 0;
		Cell cell = row.createCell(cursor++, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(record.getDate());
		Sheet sheet = cell.getRow().getSheet();
		Workbook wb = sheet.getWorkbook();
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat()
				.getFormat("yyyy-MM-dd"));
		cell.setCellStyle(cellStyle);
	
		row.createCell(cursor++).setCellValue(record.getFinanceId());
		row.createCell(cursor++).setCellValue(record.getSummary());
		row.createCell(cursor++).setCellValue(getAmount(record));
	
		for (int i = 0; i < cursor; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	@Override
	protected String[] getHeader() {
		return new String[] { "财务日期", "凭证号", "财务凭证摘要", "金额" };
	}

	@Override
	protected String getMatchedRecordsProperty() {
		return OutputMatchedRecordsTasklet.MATCHED_FINANCE_RECORDS;
	}

	@Override
	protected String getRecordsProperty() {
		return FinanceRecordProcessor.PROCESSED_FINANCE_RECORDS;
	}

}
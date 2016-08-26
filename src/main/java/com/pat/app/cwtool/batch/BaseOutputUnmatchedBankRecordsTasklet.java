package com.pat.app.cwtool.batch;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.pat.app.cwtool.model.BankRecord;

public abstract class BaseOutputUnmatchedBankRecordsTasklet extends
		BaseOuputUnmatchedRecordsTasklet<BankRecord> {

	@Override
	protected String getRecordsProperty() {
		return BankRecordProcessor.PROCESSED_BANK_RECORDS;
	}

	@Override
	protected String getMatchedRecordsProperty() {
		return OutputMatchedRecordsTasklet.MATCHED_BANK_RECORDS;
	}

	@Override
	protected String[] getHeader() {
		return new String[] { "银行记账日期", "交易流水号", "银行摘要", "金额" };
	}

	@Override
	protected void createDataRow(BankRecord record, Row row) {
		int cursor = 0;
		Cell cell = row.createCell(cursor++, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(record.getDate());
		Sheet sheet = cell.getRow().getSheet();
		Workbook wb = sheet.getWorkbook();
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat()
				.getFormat("yyyy-MM-dd"));
		cell.setCellStyle(cellStyle);

		row.createCell(cursor++).setCellValue(record.getTradeNum());
		row.createCell(cursor++).setCellValue(record.getComment());
		row.createCell(cursor++).setCellValue(getAmount(record));

		for (int i = 0; i < cursor; i++) {
			sheet.autoSizeColumn(i);
		}
	}
}
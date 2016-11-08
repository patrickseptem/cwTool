package com.pat.app.cwtool.batch;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.pat.app.cwtool.model.FinanceRecord;

public abstract class BaseOutputUnmatchedFinanceRecordsTasklet extends BaseOuputUnmatchedRecordsTasklet<FinanceRecord> {

	public BaseOutputUnmatchedFinanceRecordsTasklet() {
		super();
	}

	@Override
	protected void createDataRow(FinanceRecord record, Row row) {
		int cursor = 0;
		Cell cell = row.createCell(cursor++, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(record.getDate());
		cell.setCellStyle(dateCellStyle);

		row.createCell(cursor++).setCellValue(record.getFinanceId());
		row.createCell(cursor++).setCellValue(record.getSummary());
		row.createCell(cursor++).setCellValue(getAmount(record));

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
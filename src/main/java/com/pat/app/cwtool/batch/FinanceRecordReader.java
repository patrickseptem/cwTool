package com.pat.app.cwtool.batch;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.FinanceRecordImpl;

public class FinanceRecordReader extends BaseRecordReader<FinanceRecord> {

	@Value("#{jobParameters['financeSheetPath']}")
	public void setDataSourcePath(String path) {
		this.dataSourcePath = path;
	}

	@Override
	public FinanceRecord read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Row row = sheet.getRow(cursor++);
		if (row == null) {
			return null; // end
		}
		// TODO should read data by column type in configuration file
		short firstCellNum = row.getFirstCellNum();
		Cell cell = row.getCell(firstCellNum);
		Date date = getDate(cell);
		if (date == null) {
			throw new UnexpectedInputException(String.format("没有日期的记录: 第%s行",
					row.getRowNum() + 1));
		}
		String financeId = row.getCell(++firstCellNum).getStringCellValue();
		String summary = row.getCell(++firstCellNum).getStringCellValue();
		cell = row.getCell(++firstCellNum);
		double proceeds = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? cell
				.getNumericCellValue() : 0;
		cell = row.getCell(++firstCellNum);
		double payment = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? cell
				.getNumericCellValue() : 0;
		return new FinanceRecordImpl(row.getRowNum() + 1, date, financeId,
				summary, proceeds, payment);
	}

	private Date getDate(Cell cell) throws java.text.ParseException {
		Date date = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			String dateString = cell.getStringCellValue();
			date = dateString.isEmpty() ? null : dateFormat.parse(dateString);
			break;
		case Cell.CELL_TYPE_NUMERIC:
			date = cell.getDateCellValue();
			break;
		default:
			break;
		}
		return date;
	}

	@Override
	public String getFirstTitleName() {
		return "日期";
	}
}

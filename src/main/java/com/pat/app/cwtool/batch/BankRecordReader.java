package com.pat.app.cwtool.batch;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.BankRecordImpl;

public class BankRecordReader extends BaseRecordReader<BankRecord> {

	@Value("#{jobParameters['bankSheetPath']}")
	public void setDataSourcePath(String path) {
		this.dataSourcePath = path;
	}

	@Override
	public BankRecord read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Row row = sheet.getRow(cursor++);
		if (row == null) {
			return null; // end
		}
		// TODO should read data by column type in configuration file
		short firstCellNum = row.getFirstCellNum();
		Cell dateCell = row.getCell(firstCellNum);

		String dateString = dateCell.getStringCellValue();
		Date date = dateString.isEmpty() ? null : dateFormat.parse(dateString);
		if (date == null) {
			throw new UnexpectedInputException(String.format("没有日期的记录: 第%s行",
					row.getRowNum() + 1));
		}

		String tradeNum = row.getCell(++firstCellNum).getStringCellValue();
		Cell cell = row.getCell(++firstCellNum);
		double withdraw = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? cell
				.getNumericCellValue() : 0;
		cell = row.getCell(++firstCellNum);
		double deposit = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? cell
				.getNumericCellValue() : 0;
		String account = row.getCell(++firstCellNum).getStringCellValue();
		String summary = row.getCell(++firstCellNum).getStringCellValue();
		String comment = row.getCell(++firstCellNum).getStringCellValue();
		return new BankRecordImpl(row.getRowNum() + 1, date, tradeNum,
				withdraw, deposit, account, summary, comment);
	}

	@Override
	public String getFirstTitleName() {
		return "银行记账日期";
	}
}

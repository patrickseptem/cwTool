package com.pat.app.cwtool.batch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;

public abstract class BaseRecordReader<T> implements ItemReader<T>,
		StepExecutionListener {

	private static Logger s_logger = LoggerFactory
			.getLogger(BaseRecordReader.class);
	protected String dataSourcePath;
	protected Sheet sheet;
	protected int cursor;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
			Locale.CHINA);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(new File(dataSourcePath));
		} catch (EncryptedDocumentException | InvalidFormatException
				| IOException e) {
			s_logger.error("文件无法读取: " + dataSourcePath, e);
			return;
		}
		sheet = workbook.getSheetAt(0);
		// TODO check if sheet 0 no data
		cursor = locateFirstDataRow();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (sheet != null) {
			try {
				sheet.getWorkbook().close();
				s_logger.debug("Closed data source: " + dataSourcePath);
			} catch (IOException e) {
				s_logger.error(e.getMessage(), e);
			}
		}
		return ExitStatus.COMPLETED;
	}

	protected int locateFirstDataRow() {
		int rn = sheet.getFirstRowNum();
		for (int lrn = sheet.getLastRowNum(); rn < lrn; rn++) {
			Row row = sheet.getRow(rn);
			short firstCellNum = row.getFirstCellNum();
			if (firstCellNum == -1) {
				continue;
			}
			Cell dateCell = row.getCell(firstCellNum);
			if (Cell.CELL_TYPE_STRING == dateCell.getCellType()
					&& getFirstTitleName()
							.equals(dateCell.getStringCellValue())) {
				break;
			}
		}
		return rn + 1; // data row is next row
	}

	public abstract String getFirstTitleName();

}
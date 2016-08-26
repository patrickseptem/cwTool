package com.pat.app.cwtool.batch;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pat.app.cwtool.model.ProcessedRecord;

public abstract class BaseOuputUnmatchedRecordsTasklet<T> implements Tasklet {

	private static final String OUTPUT_FILE_EXT = ".xls";

	protected abstract void createDataRow(T record, Row row);

	protected abstract String[] getHeader();

	protected abstract String getTitle();

	protected abstract String getMatchedRecordsProperty();

	protected abstract String getRecordsProperty();

	protected abstract double getAmount(T record);

	protected abstract boolean isUnmatchedRecord(
			Set<ProcessedRecord<T>> matchedRecords,
			ProcessedRecord<T> processedRecord);

	protected void createHeaders(Row row, String[] headers) {
		for (int i = 0; i < headers.length; i++) {
			String h = headers[i];
			Cell cell = row.createCell(i);
			cell.setCellValue(h);
		}
	}

	protected void createTitle(Workbook workbook, Sheet sheet, Row row,
			String title) {
		Cell cell = row.createCell(0);
		cell.setCellValue(title);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(cellStyle);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		Map<String, Object> ctx = chunkContext.getStepContext()
				.getJobExecutionContext();
		Set<ProcessedRecord<T>> matchedBRs = (Set<ProcessedRecord<T>>) ctx
				.get(getMatchedRecordsProperty());
		List<ProcessedRecord<T>> allBRs = (List<ProcessedRecord<T>>) ctx
				.get(getRecordsProperty());
		if (matchedBRs == null || matchedBRs.isEmpty()) {
			return RepeatStatus.FINISHED;
		}

		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		// create title
		int cursor = 0;
		createTitle(workbook, sheet, sheet.createRow(cursor++), getTitle());
		createHeaders(sheet.createRow(cursor++), getHeader());

		addDataRow(matchedBRs, allBRs, sheet, cursor);

		FileOutputStream fos = new FileOutputStream(getTitle()
				+ OUTPUT_FILE_EXT);
		workbook.write(fos);
		fos.close();

		return RepeatStatus.FINISHED;
	}

	protected void addDataRow(Set<ProcessedRecord<T>> matchedBRs,
			List<ProcessedRecord<T>> allBRs, Sheet sheet, int cursor) {
		int currentCursor = cursor;
		for (ProcessedRecord<T> br : allBRs) {
			if (isUnmatchedRecord(matchedBRs, br)) {
				createDataRow(br.getRecord(), sheet.createRow(currentCursor++));
			}
		}
	}

}
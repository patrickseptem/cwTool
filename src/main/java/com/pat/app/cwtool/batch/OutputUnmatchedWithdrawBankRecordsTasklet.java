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

import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class OutputUnmatchedWithdrawBankRecordsTasklet implements Tasklet {

	private static final String TITLE = "银行已支付，单位未支付";

	@SuppressWarnings("unchecked")
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		Map<String, Object> ctx = chunkContext.getStepContext()
				.getJobExecutionContext();
		Set<ProcessedRecord<BankRecord>> matchedBRs = (Set<ProcessedRecord<BankRecord>>) ctx
				.get(OutputMatchedRecordsTasklet.MATCHED_BANK_RECORDS);
		List<ProcessedRecord<BankRecord>> allBRs = (List<ProcessedRecord<BankRecord>>) ctx
				.get(BankRecordProcessor.PROCESSED_BANK_RECORDS);
		if (matchedBRs == null || matchedBRs.isEmpty()) {
			return RepeatStatus.FINISHED;
		}

		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		// create title
		int cursor = 0;
		createTitle(workbook, sheet, sheet.createRow(cursor++), TITLE);
		createHeaders(sheet.createRow(cursor++), new String[] { "银行记账日期",
				"交易流水号", "银行摘要", "金额" });

		for (ProcessedRecord<BankRecord> br : allBRs) {
			if (!matchedBRs.contains(br) && br.getRecord().getWithdraw() != 0) {
				createDataRow(br.getRecord(), sheet.createRow(cursor++));
			}
		}

		FileOutputStream fos = new FileOutputStream(TITLE + ".xls");
		workbook.write(fos);
		fos.close();

		return RepeatStatus.FINISHED;
	}

	private void createDataRow(BankRecord record, Row row) {
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
		row.createCell(cursor++).setCellValue(record.getWithdraw());

		for (int i = 0; i < cursor; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void createHeaders(Row row, String[] headers) {
		for (int i = 0; i < headers.length; i++) {
			String h = headers[i];
			Cell cell = row.createCell(i);
			cell.setCellValue(h);
		}
	}

	private void createTitle(Workbook workbook, Sheet sheet, Row row,
			String title) {
		Cell cell = row.createCell(0);
		cell.setCellValue(title);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(cellStyle);
	}
}

package com.pat.app.cwtool.batch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pat.app.cwtool.match.MatchManager;
import com.pat.app.cwtool.match.MatchRecord;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class OutputMatchedRecordsTasklet implements Tasklet {

	private static final int MAX_FACTOR_THRESHOLD = 7;

	public static final String MATCHED_FINANCE_RECORDS = "matchedFinanceRecords";

	public static final String MATCHED_BANK_RECORDS = "matchedBankRecords";

	public static final String ALL_BEST_MATCHES = "allBestMatches";

	private static final Logger s_logger = LoggerFactory.getLogger(OutputMatchedRecordsTasklet.class);

	@Autowired
	private MatchManager matchManager;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> matchedRecords = matchManager.getMatcheRecords();
		List<MatchRecord> allBestMatches = new ArrayList<>();
		List<MatchRecord> allPossibleMatches = new ArrayList<>();

		short max;
		List<MatchRecord> bestMatches = new ArrayList<MatchRecord>();
		for (Entry<ProcessedRecord<BankRecord>, Set<MatchRecord>> entry : matchedRecords.entrySet()) {
			max = MAX_FACTOR_THRESHOLD;
			bestMatches.clear();
			for (MatchRecord mr : entry.getValue()) {
				if (mr.getFactor() > max) {
					max = mr.getFactor();
					bestMatches.clear();
					bestMatches.add(mr);
				} else if (mr.getFactor() == max) {
					bestMatches.add(mr);
				}
			}
			// s_logger.debug(bestMatches.toString());
			if (bestMatches.size() == 1) {
				allBestMatches.add(bestMatches.get(0));
			} else if (bestMatches.size() > 1) {
				allPossibleMatches.addAll(bestMatches);
			}
		}

		Set<ProcessedRecord<BankRecord>> matchedBRs = new LinkedHashSet<>();
		Set<ProcessedRecord<FinanceRecord>> matchedFRs = new LinkedHashSet<>();
		for (MatchRecord mr : allBestMatches) {
			matchedBRs.add(mr.getBankRecord());
			matchedFRs.add(mr.getFinanceRecord());
		}

		ExecutionContext ctx = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		ctx.put(MATCHED_BANK_RECORDS, matchedBRs);
		ctx.put(MATCHED_FINANCE_RECORDS, matchedFRs);

		s_logger.debug("output all best matches to a intermediate matched.xls file.");
		output2Excel(matchedRecords);

		return RepeatStatus.FINISHED;
	}

	private void output2Excel(Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> matchedRecords)
			throws FileNotFoundException, IOException {
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		Font blueFont = workbook.createFont();
		blueFont.setFontName(XSSFFont.DEFAULT_FONT_NAME);
		blueFont.setColor(IndexedColors.BLUE.getIndex());

		Font greenFont = workbook.createFont();
		greenFont.setFontName(XSSFFont.DEFAULT_FONT_NAME);
		greenFont.setColor(IndexedColors.GREEN.getIndex());

		CellStyle blueCStyle = workbook.createCellStyle();
		blueCStyle.setFont(blueFont);

		CellStyle greenCStyle = workbook.createCellStyle();
		greenCStyle.setFont(greenFont);

		CellStyle blueDateCStyle = createDateCellStyle(workbook, blueFont);
		CellStyle greenDateCStyle = createDateCellStyle(workbook, greenFont);

		int cursor = 0;
		String title = "银行和财务匹配记录";
		createTitle(workbook, sheet, sheet.createRow(cursor++), title);
		createHeaders(sheet.createRow(cursor++),
				new String[] { "", "行号", "银行记账日期", "交易流水号", "银行支付金额 ", "银行收款金额", "对方户名", "摘要", "备注" }, blueCStyle);
		createHeaders(sheet.createRow(cursor++), new String[] { "匹配因子", "行号", "日期", "凭证号", "单位收款金额", "单位付款金额", "凭证摘要" },
				greenCStyle);

		int printMatchedNum = 3;
		for (Entry<ProcessedRecord<BankRecord>, Set<MatchRecord>> entry : matchedRecords.entrySet()) {
			createSplitterRow(sheet, cursor++);

			BankRecord br = entry.getKey().getRecord();
			addBankRecord(br, sheet.createRow(cursor++), blueDateCStyle, blueCStyle);

			TreeSet<MatchRecord> machedFRs = (TreeSet<MatchRecord>) entry.getValue();
			Iterator<MatchRecord> it = machedFRs.descendingIterator();
			for (int i = 0; i < printMatchedNum && it.hasNext(); i++) {
				MatchRecord mr = it.next();
				addFinanceRecord(mr, sheet.createRow(cursor++), greenDateCStyle, greenCStyle);
			}
		}

		// 8 is column num
		for (int i = 0; i < 8; i++) {
			sheet.autoSizeColumn(i);
		}

		sheet.createFreezePane(0, 3);

		FileOutputStream fos = new FileOutputStream(title + ".xls");
		try {
			workbook.write(fos);
		} finally {
			fos.close();
		}
	}

	private void createSplitterRow(Sheet sheet, int cursor) {
		Row row = sheet.createRow(cursor);
		row.setHeight((short) 30);
		// row.createCell(0).setCellValue("-");;
	}

	private void addFinanceRecord(MatchRecord mr, Row row, CellStyle dateCStyle, CellStyle style) {
		int cursor = 0;
		createCell(row, cursor++, style).setCellValue(mr.getFactor());

		FinanceRecord fr = mr.getFinanceRecord().getRecord();
		createCell(row, cursor++, style).setCellValue(fr.getRowNum());

		Cell cell = row.createCell(cursor++, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(fr.getDate());
		cell.setCellStyle(dateCStyle);

		createCell(row, cursor++, style).setCellValue(fr.getFinanceId());
		createCell(row, cursor++, style).setCellValue(fr.getProceeds());
		createCell(row, cursor++, style).setCellValue(fr.getPayment());
		createCell(row, cursor++, style).setCellValue(fr.getSummary());
	}

	private CellStyle createDateCellStyle(Workbook workbook, Font font) {
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));
		dateCellStyle.setFont(font);
		return dateCellStyle;
	}

	private void addBankRecord(BankRecord br, Row row, CellStyle dateCStyle, CellStyle style) {
		int cursor = 0;
		// blank cell
		createCell(row, cursor++, style);

		createCell(row, cursor++, style).setCellValue(br.getRowNum());

		Cell cell = row.createCell(cursor++, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(br.getDate());
		cell.setCellStyle(dateCStyle);

		createCell(row, cursor++, style).setCellValue(br.getTradeNum());

		cell = createCell(row, cursor++, style);
		if (br.getWithdraw() == 0) {
			cell.setCellValue("--");
		} else {
			cell.setCellValue(br.getWithdraw());
		}

		cell = createCell(row, cursor++, style);
		if (br.getDeposit() == 0) {
			cell.setCellValue("--");
		} else {
			cell.setCellValue(br.getDeposit());
		}

		createCell(row, cursor++, style).setCellValue(br.getAccount());
		createCell(row, cursor++, style).setCellValue(br.getSummary());
		createCell(row, cursor++, style).setCellValue(br.getComment());

	}

	private Cell createCell(Row row, int cursor, CellStyle style) {
		Cell cell = row.createCell(cursor);
		cell.setCellStyle(style);
		return cell;
	}

	protected void createHeaders(Row row, String[] headers, CellStyle style) {
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(style);
		}
	}

	protected void createTitle(Workbook workbook, Sheet sheet, Row row, String title) {
		Cell cell = row.createCell(0);
		cell.setCellValue(title);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(cellStyle);
	}

}

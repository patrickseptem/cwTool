package com.pat.app.cwtool.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pat.app.cwtool.analyzer.Keyword;
import com.pat.app.cwtool.match.MatchManager;
import com.pat.app.cwtool.match.MatchRecord;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class CollectCompanyUnpaidRecordsTasklet implements Tasklet {

	private static final Logger s_logger = LoggerFactory
			.getLogger(CollectCompanyUnpaidRecordsTasklet.class);

	private static final double CALIBRATION = 0.00001;
	private List<ProcessedRecord<BankRecord>> bankRecords;
	private List<ProcessedRecord<FinanceRecord>> financeRecords;

	@Autowired
	MatchManager matchManager;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		final int len = bankRecords.size();
		final int len2 = financeRecords.size();
		ProcessedRecord<BankRecord> br;
		ProcessedRecord<FinanceRecord> fr;
		List<? extends Keyword> allFinanceKeywords;
		List<? extends Keyword> allBankKeywords;
		Keyword nameKw = null;
		short factor; // match factor of 2 records, big value means match a lot
		boolean isWithdraw;
		for (int i = 0; i < len; i++) {
			br = bankRecords.get(i);
			isWithdraw = br.getRecord().getWithdraw() != 0;
			allBankKeywords = br.getKeywords()
					.get(ProcessedRecord.ALL_KEYWORDS);

			if (br.getKeywords().get(BankRecord.NAME) != null) {
				nameKw = br.getKeywords().get(BankRecord.NAME).get(0);
			} else {
				nameKw = null;
			}
			for (int j = 0; j < len2; j++) {
				fr = financeRecords.get(j);
				MatchRecord matchRecord = matchManager
						.createMatchRecord(br, fr);
				if (isWithdraw ^ (fr.getRecord().getPayment() != 0)) {
					continue;
				}
				factor = 0;

				if (isWithdraw
						&& (Math.abs(br.getRecord().getWithdraw()
								- fr.getRecord().getPayment()) <= CALIBRATION)) {
					factor += 2;
				} else if (!isWithdraw
						&& (Math.abs(br.getRecord().getDeposit()
								- fr.getRecord().getProceeds()) <= CALIBRATION)) {
					factor += 2;
				}

				allFinanceKeywords = fr.getKeywords().get(
						ProcessedRecord.ALL_KEYWORDS);
				for (Keyword fkw : allFinanceKeywords) {
					if (allBankKeywords.contains(fkw)) {
						factor++;
						matchRecord.addMatchKeyword(fkw);
					}
				}

				if (nameKw != null) {
					if (!fr.getRecord().getSummary()
							.contains(nameKw.getValue())) {
						factor -= 20;
					} else {
						matchRecord.addMatchKeyword(nameKw);
					}
				}

				if (factor > 0) {
					matchRecord.setFactor(factor);
					matchManager.addMatchRecord(matchRecord);
				}
			}
		}

		s_logger.info(String.format("%s条银行记录有相关财务记录",
				matchManager.getMatcheRecordsSize()));

		return RepeatStatus.FINISHED;
	}

	@SuppressWarnings("unchecked")
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		bankRecords = (List<ProcessedRecord<BankRecord>>) stepExecution
				.getJobExecution().getExecutionContext()
				.get(BankRecordProcessor.PROCESSED_BANK_RECORDS);
		financeRecords = (List<ProcessedRecord<FinanceRecord>>) stepExecution
				.getJobExecution().getExecutionContext()
				.get(FinanceRecordProcessor.PROCESSED_FINANCE_RECORDS);
	}
}

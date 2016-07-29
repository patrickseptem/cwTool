package com.pat.app.cwtool.batch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pat.app.cwtool.analyzer.Keyword;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class CollectCompanyUnpaidRecordsTasklet implements Tasklet {

	private static final Logger s_logger = LoggerFactory
			.getLogger(CollectCompanyUnpaidRecordsTasklet.class);

	private static final double CALIBRATION = 0.00001;
	private List<ProcessedRecord<BankRecord>> bankRecords;
	private List<ProcessedRecord<FinanceRecord>> financeRecords;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {

		Map<ProcessedRecord<BankRecord>, Map<ProcessedRecord<FinanceRecord>, Short>> factors = new LinkedHashMap<>();

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
			}
			for (int j = 0; j < len2; j++) {
				fr = financeRecords.get(j);
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
					}
				}

				if (nameKw != null) {
					if (!fr.getRecord().getSummary()
							.contains(nameKw.getValue())) {
						factor -= 20;
					}
				}

				if (factor > 0) {
					Map<ProcessedRecord<FinanceRecord>, Short> fs = factors
							.get(br);
					if (fs == null) {
						fs = new LinkedHashMap<>();
						factors.put(br, fs);
					}
					fs.put(fr, factor);
				}
			}
		}

		s_logger.debug(String.format("%s条银行记录有相关财务记录", factors.size()));
		Set<Entry<ProcessedRecord<BankRecord>, Map<ProcessedRecord<FinanceRecord>, Short>>> entrySet = factors
				.entrySet();
		for (Entry<ProcessedRecord<BankRecord>, Map<ProcessedRecord<FinanceRecord>, Short>> entry : entrySet) {
			System.out.println(entry.getKey().getRecord());
			Set<Entry<ProcessedRecord<FinanceRecord>, Short>> es = entry
					.getValue().entrySet();
			short max = 0;
			FinanceRecord fr1 = null;
			for (Entry<ProcessedRecord<FinanceRecord>, Short> e2 : es) {
				if (e2.getValue() > max) {
					max = e2.getValue();
					fr1 = e2.getKey().getRecord();
				}
			}
			System.out.println(fr1);
			System.out.println("----匹配因子值:" + max);
		}
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

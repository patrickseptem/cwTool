package com.pat.app.cwtool.batch;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	public static final String MATCHED_FINANCE_RECORDS = "matchedFinanceRecords";

	public static final String MATCHED_BANK_RECORDS = "matchedBankRecords";

	public static final String ALL_BEST_MATCHES = "allBestMatches";

	private static final Logger s_logger = LoggerFactory
			.getLogger(OutputMatchedRecordsTasklet.class);

	@Autowired
	private MatchManager matchManager;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> matcheRecords = matchManager
				.getMatcheRecords();
		Set<Entry<ProcessedRecord<BankRecord>, Set<MatchRecord>>> entrySet = matcheRecords
				.entrySet();
		List<MatchRecord> allBestMatches = new ArrayList<>();
		List<MatchRecord> allPossibleMatches = new ArrayList<>();

		short max = 0;
		List<MatchRecord> bestMatches = null;
		for (Entry<ProcessedRecord<BankRecord>, Set<MatchRecord>> entry : entrySet) {
			max = 0;
			bestMatches = new ArrayList<MatchRecord>();
			for (MatchRecord mr : entry.getValue()) {
				if (mr.getFactor() > max) {
					max = mr.getFactor();
					bestMatches.clear();
					bestMatches.add(mr);
				} else if (mr.getFactor() == max) {
					bestMatches.add(mr);
				}
			}
			s_logger.debug(bestMatches.toString());
			if (bestMatches.size() == 1) {
				allBestMatches.add(bestMatches.get(0));
			} else {
				allPossibleMatches.addAll(bestMatches);
			}
		}

		Set<ProcessedRecord<BankRecord>> matchedBRs = new LinkedHashSet<>();
		Set<ProcessedRecord<FinanceRecord>> matchedFRs = new LinkedHashSet<>();
		for (MatchRecord mr : allBestMatches) {
			matchedBRs.add(mr.getBankRecord());
			matchedFRs.add(mr.getFinanceRecord());
		}

		ExecutionContext ctx = chunkContext.getStepContext().getStepExecution()
				.getJobExecution().getExecutionContext();
		ctx.put(MATCHED_BANK_RECORDS, matchedBRs);
		ctx.put(MATCHED_FINANCE_RECORDS, matchedFRs);
		// TODO output all best matches to a intermediate matched.xls file.

		return RepeatStatus.FINISHED;
	}
}

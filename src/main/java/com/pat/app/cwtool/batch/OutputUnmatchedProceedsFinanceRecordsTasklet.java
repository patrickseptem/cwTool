package com.pat.app.cwtool.batch;

import java.util.Set;

import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class OutputUnmatchedProceedsFinanceRecordsTasklet extends
		BaseOutputUnmatchedFinanceRecordsTasklet {

	@Override
	protected String getTitle() {
		return "单位已支付，银行未支付";
	}

	@Override
	protected boolean isUnmatchedRecord(
			Set<ProcessedRecord<FinanceRecord>> matchedFRs,
			ProcessedRecord<FinanceRecord> fr) {
		return !matchedFRs.contains(fr) && fr.getRecord().getProceeds() != 0;
	}

	@Override
	protected double getAmount(FinanceRecord record) {
		return record.getProceeds();
	}

}

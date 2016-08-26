package com.pat.app.cwtool.batch;

import java.util.Set;

import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class OutputUnmatchedPaymentFinanceRecordsTasklet extends
		OutputUnmatchedFinanceRecordsTasklet {

	@Override
	protected String getTitle() {
		return "单位已收款，银行未收款";
	}

	@Override
	protected double getAmount(FinanceRecord record) {
		return record.getPayment();
	}

	@Override
	protected boolean isUnmatchedRecord(
			Set<ProcessedRecord<FinanceRecord>> matchedFRs,
			ProcessedRecord<FinanceRecord> pr) {
		return !matchedFRs.contains(pr) && pr.getRecord().getPayment() != 0;
	}

}

package com.pat.app.cwtool.batch;

import java.util.Set;

import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class OutputUnmatchedDepositBankRecordsTasklet extends
		BaseOutputUnmatchedBankRecordsTasklet {

	@Override
	protected boolean isUnmatchedRecord(
			Set<ProcessedRecord<BankRecord>> matchedBRs,
			ProcessedRecord<BankRecord> br) {
		return !matchedBRs.contains(br) && br.getRecord().getWithdraw() == 0;
	}

	@Override
	protected String getTitle() {
		return "银行已收款，单位未收款";
	}

	@Override
	protected double getAmount(BankRecord record) {
		return record.getDeposit();
	}
}

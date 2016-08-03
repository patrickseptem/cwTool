package com.pat.app.cwtool.match;

import java.util.LinkedHashSet;
import java.util.Set;

import com.pat.app.cwtool.analyzer.Keyword;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class MatchRecord {
	private ProcessedRecord<BankRecord> bankRecord;
	private ProcessedRecord<FinanceRecord> financeRecord;
	private short factor = 0;
	private Set<Keyword> matchKeywords = new LinkedHashSet<>();

	MatchRecord(ProcessedRecord<BankRecord> bankRecord,
			ProcessedRecord<FinanceRecord> financeRecord) {
		this.bankRecord = bankRecord;
		this.financeRecord = financeRecord;
	}

	public ProcessedRecord<BankRecord> getBankRecord() {
		return bankRecord;
	}

	public void setBankRecord(ProcessedRecord<BankRecord> bankRecord) {
		this.bankRecord = bankRecord;
	}

	public ProcessedRecord<FinanceRecord> getFinanceRecord() {
		return financeRecord;
	}

	public void setFinanceRecord(ProcessedRecord<FinanceRecord> financeRecord) {
		this.financeRecord = financeRecord;
	}

	public short getFactor() {
		return factor;
	}

	public void setFactor(short factor) {
		this.factor = factor;
	}

	public Set<Keyword> getMatchKeywords() {
		return matchKeywords;
	}

	public void setMatchKeywords(Set<Keyword> matchKeywords) {
		this.matchKeywords = matchKeywords;
	}

	public boolean addMatchKeyword(Keyword kw) {
		return matchKeywords.add(kw);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bankRecord == null) ? 0 : bankRecord.hashCode());
		result = prime * result + factor;
		result = prime * result
				+ ((financeRecord == null) ? 0 : financeRecord.hashCode());
		result = prime * result
				+ ((matchKeywords == null) ? 0 : matchKeywords.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchRecord other = (MatchRecord) obj;
		if (bankRecord == null) {
			if (other.bankRecord != null)
				return false;
		} else if (!bankRecord.equals(other.bankRecord))
			return false;
		if (factor != other.factor)
			return false;
		if (financeRecord == null) {
			if (other.financeRecord != null)
				return false;
		} else if (!financeRecord.equals(other.financeRecord))
			return false;
		if (matchKeywords == null) {
			if (other.matchKeywords != null)
				return false;
		} else if (!matchKeywords.equals(other.matchKeywords))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatchRecord [\nbankRecord=" + bankRecord.getRecord()
				+ "\nfinanceRecord=" + financeRecord.getRecord() + "\nfactor="
				+ factor + "\nmatchKeywords=" + matchKeywords + "]";
	}

}

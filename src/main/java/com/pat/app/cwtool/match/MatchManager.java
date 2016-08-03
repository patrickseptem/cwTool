package com.pat.app.cwtool.match;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class MatchManager {

	private Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> matches = new LinkedHashMap<>();

	public MatchRecord createMatchRecord(
			ProcessedRecord<BankRecord> bankRecord,
			ProcessedRecord<FinanceRecord> financeRecord) {
		MatchRecord matchRecord = new MatchRecord(bankRecord, financeRecord);
		return matchRecord;
	}

	public Set<MatchRecord> getMatchRecord(
			ProcessedRecord<BankRecord> bankRecord) {
		return matches.get(bankRecord);
	}

	public boolean addMatchRecord(MatchRecord record) {
		Set<MatchRecord> records = matches.get(record.getBankRecord());
		if (records == null) {
			records = new LinkedHashSet<MatchRecord>();
			matches.put(record.getBankRecord(), records);
		}
		return records.add(record);
	}

	public Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> getMatcheRecords() {
		return Collections.unmodifiableMap(matches);
	}

	public int getMatcheRecordsSize() {
		return matches.size();
	}
}

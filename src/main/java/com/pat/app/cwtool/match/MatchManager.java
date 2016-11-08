package com.pat.app.cwtool.match;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

public class MatchManager {

	private final class MatchRecordComparator implements Comparator<MatchRecord> {
		@Override
		public int compare(MatchRecord r1, MatchRecord r2) {
			return r1.getFactor() - r2.getFactor();
		}
	}

	private Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> brMatches = new LinkedHashMap<>();
	private Map<ProcessedRecord<FinanceRecord>, MatchRecord> frMatches = new LinkedHashMap<>();

	public MatchRecord createMatchRecord(ProcessedRecord<BankRecord> bankRecord,
			ProcessedRecord<FinanceRecord> financeRecord) {
		MatchRecord matchRecord = new MatchRecord(bankRecord, financeRecord);
		return matchRecord;
	}

	public Set<MatchRecord> getMatchRecord(ProcessedRecord<BankRecord> bankRecord) {
		return brMatches.get(bankRecord);
	}

	public boolean addMatchRecord(MatchRecord record) {
		ProcessedRecord<FinanceRecord> fr = record.getFinanceRecord();

		MatchRecord mr = frMatches.get(fr);
		if (mr == null) {
			frMatches.put(fr, record);
		} else {
			short factor = record.getFactor();
			short max = mr.getFactor();
			if (factor > max) {
				brMatches.get(mr.getBankRecord()).remove(mr);
				frMatches.put(fr, record);
			} else if (factor <= max) {
				return false;
			}
		}

		Set<MatchRecord> matchRecords = brMatches.get(record.getBankRecord());
		if (matchRecords == null) {
			matchRecords = new TreeSet<MatchRecord>(new MatchRecordComparator());
			brMatches.put(record.getBankRecord(), matchRecords);
		}
		return matchRecords.add(record);
	}

	public Map<ProcessedRecord<BankRecord>, Set<MatchRecord>> getMatcheRecords() {
		return Collections.unmodifiableMap(brMatches);
	}

	public int getMatcheRecordsSize() {
		return brMatches.size();
	}
}
